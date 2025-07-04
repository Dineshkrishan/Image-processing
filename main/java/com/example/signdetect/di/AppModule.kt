package com.example.signdetect.di

import android.content.Context
import com.example.signdetect.domain.comparison.SignatureComparator
import com.example.signdetect.domain.processing.SignatureProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideSignatureProcessor(): SignatureProcessor {
        return SignatureProcessor()
    }
    
    @Provides
    @Singleton
    fun provideSignatureComparator(
        signatureProcessor: SignatureProcessor
    ): SignatureComparator {
        return SignatureComparator(signatureProcessor)
    }
} 