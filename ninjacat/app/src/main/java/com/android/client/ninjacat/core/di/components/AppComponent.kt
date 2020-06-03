import android.app.Application
import com.android.client.ninjacat.AppController
import com.android.client.ninjacat.core.di.modules.ActivityModule
import com.android.client.ninjacat.core.di.modules.ApiModule
import com.android.client.ninjacat.core.di.modules.AuthFragmentModule
import com.android.client.ninjacat.core.di.modules.DbModule
import com.android.client.ninjacat.core.di.modules.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/*
 * We mark this interface with the @Component annotation.
 * And we define all the modules that can be injected.
 * Note that we provide AndroidSupportInjectionModule.class
 * here. This class was not created by us.
 * It is an internal class in Dagger 2.10.
 * Provides our activities and fragments with given module.
 * */
@Component(
    modules = [
        ApiModule::class,
        DbModule::class,
        ViewModelModule::class,
        ActivityModule::class,
        AuthFragmentModule::class,
        AndroidSupportInjectionModule::class]
)
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(appController: AppController)
}

