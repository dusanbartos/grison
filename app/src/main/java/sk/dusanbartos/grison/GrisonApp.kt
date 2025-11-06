package sk.dusanbartos.grison

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import sk.dusanbartos.grison.domain.cards.CardsRepository
import javax.inject.Inject

@HiltAndroidApp
class GrisonApp : Application() {

    @Inject
    lateinit var cardsRepository: CardsRepository

    override fun onCreate() {
        super.onCreate()

        //FirebaseApp.initializeApp(this)

        cardsRepository.start()
    }
}