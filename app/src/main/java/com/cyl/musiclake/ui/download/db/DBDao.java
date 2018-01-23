package com.cyl.musiclake.ui.download.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cyl.musiclake.ui.download.model.DownloadInfo;
import com.cyl.musiclake.ui.download.model.FileState;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class DBDao {

    private DBHelper dbHelper;

    public DBDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 根据地址判断是否是第一次下载
     *
     * @param url 下载地址
     * @return true 第一次下载 false 再次下载
     */
    public synchronized boolean isExistDownloadInfo(String url) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        int count = -1;
        Cursor cursor = null;
        try {
            // 返回指定列不同值的数目
            String sql = "select count(*)  from download_info where url=?";
            cursor = database.rawQuery(sql, new String[]{url});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
            if (null != cursor) {
                cursor.close();
            }
        }
        return count == 0;
    }

    /**
     * 保存下载的具体信息
     *
     * @param infos 下载的信息
     */
    public synchronized void saveDownloadInfos(List<DownloadInfo> infos) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            for (DownloadInfo info : infos) {
                String sql = "insert into download_info(thread_id,start_pos, end_pos,complete_size,url) values (?,?,?,?,?)";
                Object[] bindArgs = {info.getThreadId(), info.getStartPos(),
                        info.getEndPos(), info.getCompleteSize(),
                        info.getUrl()};
                database.execSQL(sql, bindArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 根据地址获取下载的具体信息
     *
     * @param url 下载地址
     * @return List<BaiduSongInfo>(下载器id/线程id、开始下载的节点、结束下载的节点、完成的进度、下载器网络标识/下载地址)
     */
    public synchronized List<DownloadInfo> getDownloadInfos(String url) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        List<DownloadInfo> list = new ArrayList<DownloadInfo>();
        Cursor cursor = null;
        try {
            String sql = "select thread_id, start_pos, end_pos,complete_size,url from download_info where url=?";
            cursor = database.rawQuery(sql, new String[]{url});
            while (cursor.moveToNext()) {
                DownloadInfo info = new DownloadInfo(cursor.getInt(0),
                        cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                        cursor.getString(4));
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
            if (null != cursor) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 根据线程id以及地址更新下载的进度
     *
     * @param threadId     线程id
     * @param completeSize 下载完成的进度
     * @param url          下载地址
     */
    public synchronized void updateDownloadInfo(int threadId,
                                                long completeSize, String url) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            String sql = "update download_info set complete_size=? where thread_id=? and url=?";
            Object[] bindArgs = {completeSize, threadId, url};
            database.execSQL(sql, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 根据地址删除下载信息
     *
     * @param url 下载的地址
     */
    public synchronized void delete(String url) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            database.delete("download_info", "url=?", new String[]{url});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 保存文件的状态记录
     *
     * @param fileState 文件状态信息
     */
    public synchronized void saveFileState(FileState fileState) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            String sql = "insert into download_file(mid,name,url, state,complete_size,file_size) values (?,?,?,?,?,?)";
            Object[] bindArgs = {fileState.getMid(), fileState.getName(), fileState.getUrl(),
                    fileState.getState(), fileState.getFinish(),
                    fileState.getFileSize()};
            database.execSQL(sql, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 获取所有的下载文件的状态信息
     *
     * @return
     */
    public synchronized List<FileState> getFileStates() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        List<FileState> list = new ArrayList<FileState>();
        Cursor cursor = null;
        try {
            String sql = "select mid, name,url, state,complete_size,file_size from download_file";
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                FileState fileState = new FileState(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
                if (cursor.getInt(3) == 1) {
                    list.add(fileState);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
            if (null != cursor) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 获取所有的下载完文件的状态信息
     *
     * @return
     */
    public synchronized List<FileState> getFileStated() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        List<FileState> list = new ArrayList<FileState>();
        Cursor cursor = null;
        try {
            String sql = "select mid,name,url, state,complete_size,file_size from download_file";
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                FileState fileState = new FileState(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
                if (cursor.getInt(3) == 0) {
                    list.add(fileState);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
            if (null != cursor) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 根据地址查询表download_file中的下载文件的状态信息
     *
     * @param url 下载地址
     * @return
     */
    public synchronized FileState query(String url) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        FileState fileState = null;
        try {
            String sql = "select mid,name,url, state,complete_size,file_size from download_file";
            cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                fileState = new FileState(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
            if (null != cursor) {
                cursor.close();
            }
        }
        return fileState;
    }

    /**
     * 根据url更新状态信息（设置为下载完成）3为正在下载(暂停)，1为正在下载，0为已经下载完成
     *
     * @param url 下载地址
     */
    public synchronized void updateStateByUrl(String url, int status) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            String sql = "update download_file set state=? where url=?";
            Object[] bindArgs = {status, url};
            database.execSQL(sql, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 更新文件状态中的进度及下载状态信息
     */
    public synchronized void updateFileState(String url, int progress) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
//            for (FileState fileState : list) {
            String sql = "update download_file set complete_size=?where url=?";
            Object[] bindArgs = {progress, url};
            database.execSQL(sql, bindArgs);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 根据地址删除下载文件状态信息
     *
     * @param url 下载的地址
     */
    public synchronized void deleteFileState(String url) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            database.delete("download_file", "url=?", new String[]{url});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        if (null != dbHelper) {
            dbHelper.close();
        }
    }
}
