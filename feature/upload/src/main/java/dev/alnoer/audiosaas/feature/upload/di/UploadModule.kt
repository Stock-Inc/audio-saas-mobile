package dev.alnoer.audiosaas.feature.upload.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alnoer.audiosaas.feature.upload.FileUploadRepository
import dev.alnoer.audiosaas.feature.upload.UploadFileReader
import dev.alnoer.audiosaas.feature.upload.data.ContentResolverUploadFileReader
import dev.alnoer.audiosaas.feature.upload.data.KtorFileUploadRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UploadModule {

    @Binds
    @Singleton
    abstract fun bindFileUploadRepository(
        implementation: KtorFileUploadRepository,
    ): FileUploadRepository

    @Binds
    abstract fun bindUploadFileReader(
        implementation: ContentResolverUploadFileReader,
    ): UploadFileReader
}
