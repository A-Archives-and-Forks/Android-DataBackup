package com.xayah.databackup.rootservice;

import com.xayah.databackup.parcelables.BytesParcelable;
import com.xayah.databackup.parcelables.StatFsParcelable;
import com.xayah.databackup.parcelables.FilePathParcelable;

interface IRemoteRootService {
    void testConnection();
    ParcelFileDescriptor getInstalledAppInfos();
    ParcelFileDescriptor getInstalledAppStorages();
    List<UserInfo> getUsers();
    List<BytesParcelable> getPrivilegedConfiguredNetworks();
    int[] addNetworks(in List<BytesParcelable> configs);
    StatFsParcelable readStatFs(String path);
    List<FilePathParcelable> listFilePaths(String path, boolean listFiles, boolean listDirs);
    ParcelFileDescriptor readText(String path);
    void writeText(String path, in ParcelFileDescriptor pfd);
    long calculateTreeSize(String path);
    int callTarCli(String stdOut, String stdErr, in String[] argv);
    List<String> getPackageSourceDir(String packageName, int userId);
    String compress(int level, String inputPath, String outputPath);
    boolean mkdirs(String path);
    boolean exists(String path);
}
