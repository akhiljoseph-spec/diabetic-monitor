package com.diabeticmonitor.app.di

import android.content.Context
import androidx.room.Room
import com.diabeticmonitor.app.data.db.AppDatabase
import com.diabeticmonitor.app.data.db.dao.GlucoseReadingDao
import com.diabeticmonitor.app.data.db.dao.MedicationEntryDao
import com.diabeticmonitor.app.data.db.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGlucoseReadingDao(db: AppDatabase): GlucoseReadingDao = db.glucoseReadingDao()

    @Provides
    fun provideMedicationEntryDao(db: AppDatabase): MedicationEntryDao = db.medicationEntryDao()

    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()
}
