package com.example.android.unscramble.di

import com.example.android.unscramble.ui.game.GameFragment
import dagger.Subcomponent

@Subcomponent
interface GameComponent {

    // component를 activity에서 어떻게 생성할지를 알려줌.
    @Subcomponent.Factory
    interface Factory{
        fun create(): GameComponent
    }

    // gameFragment를 injection해서 해당 컴포넌트에 주입해서, gameFragment를 사용할 수 있음.
    fun inject(gameFragment: GameFragment){

    }
}