package com.projects.melih.helpcity;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.melih.helpcity.model.Vote;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by melih on 12.12.2017
 */

public class FirebaseDatabaseHelper {
    private static FirebaseDatabaseHelper instance;
    private final DatabaseReference reference;
    private FirebaseUser user;

    public FirebaseDatabaseHelper() {
        reference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseDatabaseHelper getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new FirebaseDatabaseHelper();
                }
            }
        }
        return instance;
    }

    public void sendLocationVote(@NonNull Vote vote) {
        String voteId = vote.getVoteId();
        Map<String, Object> postValues = vote.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/votes/" + voteId, postValues);
        childUpdates.put("/user-votes/" + user.getUid() + "/" + voteId, postValues);
        reference.updateChildren(childUpdates);
    }

    public FirebaseUser getUser() {
        if (user == null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
        }
        return user;
    }
}
