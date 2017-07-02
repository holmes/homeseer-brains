package holmes.ponderosa.transformer

import com.google.gson.Gson
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class TransformerModule {
  @Singleton @Provides fun moshi(): Moshi = Moshi.Builder().build()
  @Singleton @Provides fun jsonTransformer(gson: Gson) = JsonTransformer(gson)
}
