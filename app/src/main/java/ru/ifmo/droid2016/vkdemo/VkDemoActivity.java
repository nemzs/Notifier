package ru.ifmo.droid2016.vkdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKScopes;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ru.ifmo.droid2016.vkdemo.GroupListDataBase.GroupListDB;
import ru.ifmo.droid2016.vkdemo.loader.LoadResult;
import ru.ifmo.droid2016.vkdemo.loader.ResultType;
import ru.ifmo.droid2016.vkdemo.loader.vk.VkApiRequestLoader;
import ru.ifmo.droid2016.vkdemo.model.GroupEntry;

public class VkDemoActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<LoadResult<VKUsersArray, VKError>> {

    private TextView nameView;
    private SimpleDraweeView imageView;
    private ProgressBar progressView;
    private Button logoutButton;
    private ArrayList<GroupEntry> groups;
    VKAccessToken token;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    Button toSettings;
    Button toGroups,toLenta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initContentView();


        toSettings = (Button) findViewById(R.id.toSettings);
        toSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VkDemoActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        toLenta = (Button) findViewById(R.id.lenta);
        toLenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VkDemoActivity.this, RecyclerNews.class);
                startActivity(intent);
            }
        });
        toGroups = (Button) findViewById(R.id.groupSettings);
        toGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VkDemoActivity.this, RecyclerGroupSettings.class);
                startActivity(intent);
            }
        });
        //SharedPreferences sp = null;
        //sp = PreferenceManager.getDefaultSharedPreferences(this);
        //String s = sp.getString("time_freq_key","govno");
        //Log.d("My",s);
        // Код, который проверяет -- есть ли у нас ранее
        // сохраненный токен -- и если нет, то инициирует процедуру авторизации в Vk SDK.
        // А если есть, то сразу вызывает onLoggedIn
        Log.d("My","Tokeeen");
        token = VKAccessToken.tokenFromSharedPreferences(this, Constants.KEY_TOKEN);
        if (token != null) {
            Log.d(TAG, "onCreate: using saved token");
            onLoggedIn(token);

        } else if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: token is missing, performing login...");
            VKSdk.login(this, VKScopes.PHOTOS);
        }
    }

    protected void initContentView() {
        setContentView(R.layout.activity_vk_demo);
        nameView = (TextView) findViewById(R.id.user_name);
        imageView = (SimpleDraweeView) findViewById(R.id.user_photo);
        progressView = (ProgressBar) findViewById(R.id.progress);
        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(logoutClickListener);
    }

    /**
     * Сбрасывает все вьюшки в исходное состояние
     */
    void resetView() {
        logoutButton.setEnabled(false);
        progressView.setVisibility(View.GONE);
        nameView.setText(null);
        imageView.setImageURI((String) null);
    }


    protected void onLoggedIn(VKAccessToken token) {
        Log.d(TAG, "onLoggedIn: " + token);
        Toast.makeText(this, R.string.login_successful, Toast.LENGTH_LONG).show();
        startCurrentUserRequest();
    }

    protected void onLoginFailed(VKError error) {
        Log.w(TAG, "onLoginFailed: " + error);
        Toast.makeText(this, R.string.login_failed, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken token) {
                Log.d("My","ON RESULT");
                token.saveTokenToSharedPreferences(VkDemoActivity.this, Constants.KEY_TOKEN);
                onLoggedIn(token);
            }

            @Override
            public void onError(VKError error) {
                onLoginFailed(error);
            }
        });
    }


    void onCurrentUser(VKApiUserFull currentUser) {
        Log.d(TAG, "onCurrentUser: " + currentUser);
        nameView.setText(currentUser.first_name + " " + currentUser.last_name);
        if (!TextUtils.isEmpty(currentUser.photo_max)) {
            imageView.setImageURI(currentUser.photo_max);
        }
        VKRequest request = VKApi.groups().get(VKParameters.from(VKApiConst.USER_ID, currentUser.getId(),VKApiConst.EXTENDED,1,VKApiConst.COUNT,100));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response)  {
                JSONObject pItem = null;
                JSONArray pArr = null;
                ArrayList<GroupEntry> groupsTmp = new ArrayList<>();
                try {
                    pItem = response.json.getJSONObject("response");
                    pArr = pItem.getJSONArray("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pArr==null) return;
                for (int i = 0; i < pArr.length(); i++) {
                    JSONObject movieJS = pArr.optJSONObject(i);

                    if (movieJS != null) {
                        String name = null;
                        String avatar = null;
                        String id = null;
                        String header = null;
                        try {
                            name = movieJS.getString("name");
                            avatar = movieJS.getString("photo_50");
                            id = movieJS.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        GroupEntry item = new GroupEntry(Integer.parseInt(id),avatar,name,0);
                        groupsTmp.add(item);
                    }
                }
                for(int i=0;i<groupsTmp.size();i++) {
                    //Log.d("My", groupsTmp.get(i).id + " " + groupsTmp.get(i).name + " " + groupsTmp.get(i).avatar);
                }
                groups = groupsTmp;
                try {
                    loadGroups(groups);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                startService(new Intent(VkDemoActivity.this, GroupLoaderService.class).putExtra("token",VKAccessToken.tokenFromSharedPreferences(VkDemoActivity.this, Constants.KEY_TOKEN).accessToken));
//Do complete stuff
            }

            @Override
            public void onError(VKError error) {
                Log.d("My", "error " + error.toString());
//Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Log.d("My", "failde " + Integer.toString(attemptNumber));
//I don't really believe in progress
            }
        });
        progressView.setVisibility(View.GONE);
    }
    String scheme1;
    String dbName = "groups";
    void loadGroups(List<GroupEntry> gr) throws FileNotFoundException {
        GroupListDB gg = new GroupListDB(this.getApplicationContext(),1);
        List<GroupEntry> list = null;
        try {
            list = gg.get();
        } catch (Exception e){

        }
        if(list==null){
            gg.put(gr);
            return;
        }
        HashSet<Integer> idIgnore = new HashSet<>();
        for(GroupEntry g: list){
            //Log.d("My",Integer.toString(g.ignore));
            if(g.ignore==1)
                idIgnore.add(g.id);
        }
        gg.delete(gr);
        for(int i=0;i<gr.size();i++){
            GroupEntry f = gr.get(i);
            if(idIgnore.contains(f.id)){
                f.ignore = 1;
                gr.set(i,f);
            }
        }
        gg.put(gr);
       // if(list)
    }

    void onCurrentUserError(VKError error) {
        String errorMessage = error == null ? getString(R.string.error) : error.toString();
        onCurrentUserError(errorMessage);
    }

    void onCurrentUserError(String errorMessage) {
        Log.w(TAG, "onCurrentUserError: " + errorMessage);
        nameView.setText(errorMessage);
        progressView.setVisibility(View.GONE);
    }

    void onNoInternet() {
        Log.w(TAG, "onNoInternet");
        nameView.setText(R.string.no_internet);
        progressView.setVisibility(View.GONE);
    }

    void startCurrentUserRequest() {
        progressView.setVisibility(View.VISIBLE);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<LoadResult<VKUsersArray, VKError>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        final VKRequest request = new VKRequest("users.get", VKParameters.from(
                "fields", "photo_max,first_name,last_name"
        ));
        return new VkApiRequestLoader<>(this, request, VKUsersArray.class);
    }

    @Override
    public void onLoadFinished(Loader<LoadResult<VKUsersArray, VKError>> loader,
                               LoadResult<VKUsersArray, VKError> result) {
        Log.d("My", "onLoadFinished");
        if (result.resultType == ResultType.ERROR) {
            onCurrentUserError(result.error);
        } else if (result.resultType == ResultType.NO_INTERNET) {
            onNoInternet();
        } else if (result.resultType == ResultType.OK) {
            final VKUsersArray usersArray = result.data;
            final VKApiUserFull currentUser = usersArray == null || usersArray.size() == 0
                    ? null : usersArray.get(0);
            if (currentUser != null) {
                onCurrentUser(currentUser);
            } else {
                onCurrentUserError("Empty response");
            }
        }
    }

    public View.OnClickListener getLogoutClickListener() {
        return logoutClickListener;
    }

    @Override
    public void onLoaderReset(Loader<LoadResult<VKUsersArray, VKError>> loader) {
        Log.d(TAG, "onLoaderReset");
        resetView();
    }

    /**
     * Обработчик клика на кнопку "Выйти", выполняет процедуру logout
     */
    private View.OnClickListener logoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Context context = VkDemoActivity.this;
            Log.d("My","Logout");
            stopService(new Intent(VkDemoActivity.this, GroupLoaderService.class));

            // Выполняем логаут в Vk SDK
            VKSdk.logout();
            //Log.d("My","Service stop!");
            //stopService(new Intent(VkDemoActivity.this, GroupLoaderService.class));

            // Удаляем сохраненный токен
            VKAccessToken.removeTokenAtKey(context, Constants.KEY_TOKEN);

            // Очищаем вьюшки
            resetView();

            // Отменяем загрузку для текущего пользователя
            getSupportLoaderManager().destroyLoader(0);

            // Выкидываем пользователя на стартовый экран
            finish();
            startActivity(new Intent(VkDemoActivity.this, MainActivity.class));
        }
    };

    @Override
    protected void onDestroy() {
        Log.d("My","onDestVkDemoAc");
        super.onDestroy();
    }

    protected final String TAG = getClass().getSimpleName();

}
