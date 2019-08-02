package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.entities.AccountProto;
import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Token;
import com.treeleaf.suchi.realm.models.User;

import io.realm.Realm;

public class UserRepo extends Repo {
    private static final UserRepo userRepo;

    static {
        userRepo = new UserRepo();
    }

    public static UserRepo getInstance() {
        return userRepo;
    }

    public void saveUser(final AccountProto.LoginResponse loginResponse, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                    public void execute(Realm realm1) {
                    User user = setUser(loginResponse.getUser(), realm1);
                    realm1.copyToRealmOrUpdate(user);
                    setToken(loginResponse.getToken(), user, realm1);
                    callback.success(null);
                }
            });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            callback.fail();
        } finally {
            close(realm);
        }
    }

    private User setUser(AccountProto.User userPb, Realm realm) {
        User user = realm.where(User.class)
                .equalTo(User.USER_ID, userPb.getUserId())
                .findFirst();
        if (user != null) return user;
        return setUserField(realm.createObject(User.class, userPb.getUserId()), userPb);
    }


    private User setUserField(User user, AccountProto.User userPb) {
        user.setStoreName(userPb.getStoreName());
        user.setAddress(userPb.getAddress());
        user.setPhone(userPb.getPhone());
        user.setUserStatus(userPb.getStatus().name());
        user.setOwnerName(userPb.getOwnerName());
        user.setUserName(userPb.getUsername());

        return user;
    }

    private void setToken(String tokenStr, User user, Realm realm) {
        Token token = realm.createObject(Token.class, tokenStr);
        token.setUser(user);
    }

    public Token getToken() {
        final Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return realm.where(Token.class).findFirst();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }

    }
/*
    public void setUserStatus() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User user = realm.where(User.class).findFirst();
                    if (user != null)
                        user.setUserStatus(AccountProto.UserStatus.VERIFIED.toString());
                }
            });
        } finally {
            close(realm);
        }
    }*/

    public User getUserFromUserId(String userId) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            User user = realm.where(User.class).equalTo("userId", userId)
                    .findFirst();
            return user;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

    public User getUserFromEmailPhone(String emailPhone) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            User user = realm.where(User.class).equalTo("emailPhone", emailPhone)
                    .findFirst();
            return user;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }


    public void saveUserOnEdit(final User user, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(user);
                    callback.success(true);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            callback.fail();
        } finally {
            close(realm);
        }
    }
}
