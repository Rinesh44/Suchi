package com.treeleaf.suchi.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.treeleaf.suchi.dao.UserDao;
import com.treeleaf.suchi.realm.models.User;
import com.treeleaf.suchi.room.UserDatabase;

import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private LiveData<List<User>> allUsers;

    public UserRepository(Application application) {
        UserDatabase userDatabase = UserDatabase.getInstance(application);
        userDao = userDatabase.userDao();
        allUsers = userDao.getAllUsers();
    }

    public void insert(User user) {
        new UserRepository.InsertUserAsyncTask(userDao).execute(user);
    }

    public void update(User user) {
        new UserRepository.UpdateUserAsyncTask(userDao).execute(user);
    }

    public void delete(User user) {
        new UserRepository.DeleteUserAsyncTask(userDao).execute(user);
    }

    public void deleteAllStocks() {
        new UserRepository.DeleteAllUsersAsyncTask(userDao).execute();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }


    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public InsertUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {

            userDao.insert(users[0]);
            return null;
        }
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public UpdateUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.update(users[0]);
            return null;
        }
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        public DeleteUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.delete(users[0]);
            return null;
        }
    }

    private static class DeleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;

        public DeleteAllUsersAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllUsers();
            return null;
        }
    }

}
