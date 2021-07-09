package com.example.instagram;

import android.app.Application;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Like;
import com.example.instagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;
//testing git
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Like.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("a695Qyms87ier3XByRmVaLIel5iT8I87r1q7yWfQ")
                .clientKey("ueyCcriYqIrlAC5Rq3dUoatV4jqAh3i0C55icNVB")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
