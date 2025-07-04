package com.example.signdetect.di

import android.content.Context
import androidx.room.Room
import com.example.signdetect.data.dao.SignatureComparisonDao
import com.example.signdetect.data.database.SignatureDatabase
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
    fun provideSignatureDatabase(
        @ApplicationContext context: Context
    ): SignatureDatabase {
        return Room.databaseBuilder(
            context,
            SignatureDatabase::class.java,
            "signature_database"
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideSignatureComparisonDao(
        database: SignatureDatabase
    ): SignatureComparisonDao {
        return database.signatureComparisonDao()
    }
} 