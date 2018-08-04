package com.chl.displayscreen.database.ormHelper;

import android.content.Context;

import com.chl.displayscreen.database.orm.DaoMaster;
import com.chl.displayscreen.database.orm.DaoSession;
import com.chl.displayscreen.database.orm.PhotoInfoDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by Administrator on 2018/8/1.
 */

public class PhotoHelper {

        private static PhotoHelper instance;

        private DaoMaster daoMaster = null;
        private DaoSession daoSession = null;

        public static PhotoHelper getInstance(Context context) {//通过这个来获得session实例，就是单例
            if(instance==null){
                instance = new PhotoHelper(context);
            }
            return instance;
        }

        public PhotoHelper(Context context){
            MySqlOpenHelper openHelper = new MySqlOpenHelper(context,"ORMDB");//数据库名
            daoMaster = new DaoMaster(openHelper.getWritableDatabase());
            daoSession = daoMaster.newSession();
        }

        private PhotoInfoDao getDao() {
            return daoSession.getPhotoInfoDao();
        }

    /**
     * 向数据库中保存一条记录，如果该记录存在则更新，如果不存在则插入
     * @param info
     */
    public void save(PhotoInfo info) {
        PhotoInfo oldInfo = queryByName(info.getName());
        if (oldInfo != null) {
            info.setId(oldInfo.getId());
        }
        getDao().save(info);
    }

        /**
         * 向数据库中插入一条记录
         * @param info
         */
        public void insert(PhotoInfo info) {
            getDao().insert(info);
        }

        /**
         * 向数据库中插入一串数据
         * @param infoList
         */
        public void insertList(List<PhotoInfo> infoList){
            getDao().insertInTx(infoList);
        }

        /**
         * 删除一条记录
         * @param info
         */
        public void delete(PhotoInfo info) {
            getDao().delete(info);
        }

        /**
         * 根据id删除数据
         * @param id
         */
        public void deleteById(Long id) {
            getDao().deleteByKey(id);
        }

        /**
         * 删除所有
         */
        public void deleteAll() {
            getDao().deleteAll();
        }

        /**
         * 更新数据
         * @param info
         */
        public void update(PhotoInfo info) {
            getDao().update(info);
        }

        /**
         * 查询所有数据
         */
        public List<PhotoInfo> queryAll() {
            Query<PhotoInfo> query = getDao().queryBuilder().build();
            return query.list();
//        return queueInfoDao.loadAll();//加载全部
        }

        /**
         * 根据图片名name查询查询修改时间(唯一)
         * @param name
         */
        public PhotoInfo queryByName(String name) {
            PhotoInfo info = getDao().queryBuilder()
                    .where(PhotoInfoDao.Properties.Name.eq(name)).unique();
            return info;
        }

        /**
         * 根据队列名QueueName和序号num来查询该条队列
         * @param queueName
         * @param num
         */
//        public PhotoInfo queryByQueueNameAndNum(String queueName, int num) {
//            PhotoInfo info = getDao().queryBuilder()
//                    .where(PhotoInfoDao.Properties.QueueName.eq(queueName), PhotoInfoDao.Properties.Num.eq(num)).unique();
//            return info;
//        }

        /**
         * 根据id降序查询所有
         */
        public List<PhotoInfo> queryAllByDesc() {
            Query<PhotoInfo> query = getDao().queryBuilder()
                    .orderDesc(PhotoInfoDao.Properties.Id)//降序
                    .build();
            return query.list();
        }

        /**
         *  分页查询
         * @param pageSize 当前第几页
         * @param pageNum  每页显示多少个
         * @return
         */
        public List<PhotoInfo> queryPaging(int pageSize, int pageNum){
            Query<PhotoInfo> query = getDao().queryBuilder()
                    .offset(pageSize * pageNum).limit(pageNum)
                    .build();
            return query.list();
        }

        /**
         * 根据id降序偏移查询
         * @param offset 偏移量：从第几个开始查
         * @param limit 限制：查询多少条数据
         */
        public List<PhotoInfo> queryPagingByDesc(int offset, int limit){
            Query<PhotoInfo> query =  getDao().queryBuilder()
                    .orderDesc(PhotoInfoDao.Properties.Id)
                    .offset(offset).limit(limit)
                    .build();
            return query.list();
        }


    }
