package com.xayah.databackup

import android.app.Application
import com.xayah.databackup.data.AppRepository
import com.xayah.databackup.data.BackupConfigRepository
import com.xayah.databackup.data.CallLogRepository
import com.xayah.databackup.data.ContactRepository
import com.xayah.databackup.data.FileRepository
import com.xayah.databackup.data.MessageRepository
import com.xayah.databackup.data.NetworkRepository
import com.xayah.databackup.feature.backup.BackupSetupViewModel
import com.xayah.databackup.feature.backup.apps.AppsViewModel
import com.xayah.databackup.feature.backup.call_logs.CallLogsViewModel
import com.xayah.databackup.feature.backup.contacts.ContactsViewModel
import com.xayah.databackup.feature.backup.messages.MessagesViewModel
import com.xayah.databackup.feature.backup.networks.NetworksViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

class App : Application() {
    companion object {
        lateinit var application: Application
    }

    private val appModule = module {
        singleOf(::BackupConfigRepository) bind BackupConfigRepository::class
        singleOf(::AppRepository) bind AppRepository::class
        singleOf(::FileRepository) bind FileRepository::class
        singleOf(::NetworkRepository) bind NetworkRepository::class
        singleOf(::ContactRepository) bind ContactRepository::class
        singleOf(::CallLogRepository) bind CallLogRepository::class
        singleOf(::MessageRepository) bind MessageRepository::class

        viewModelOf(::BackupSetupViewModel)
        viewModelOf(::AppsViewModel)
        viewModelOf(::NetworksViewModel)
        viewModelOf(::ContactsViewModel)
        viewModelOf(::CallLogsViewModel)
        viewModelOf(::MessagesViewModel)
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        startKoin {
            androidLogger()
            androidContext(application)
            modules(appModule)
        }
    }
}
