package com.projects.melih.helpcity.ui.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.support.annotation.IntDef

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.transition.TransitionInflater
import android.transition.TransitionSet
import com.projects.melih.helpcity.R

/**
 * Created by melih on 1.12.2017
 */

class NavigationObserver(private val lifecycle: Lifecycle, private val supportFragmentManager: FragmentManager, private val context: Context) : LifecycleObserver {
    private var transactionToRecord: FragmentTransaction? = null

    //consider using states
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun connectListener() {
        transactionToRecord?.commit()
        transactionToRecord = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectListener() {

    }

    fun clearBackStack() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val count = supportFragmentManager.backStackEntryCount
            for (i in 0 until count) {
                supportFragmentManager.popBackStack()
            }
        }
    }

    fun openFragmentAndClearStack(newFragment: Fragment, @NavigationObserver.Companion.SlideAnimType animType: Int) {
        clearBackStack()
        replaceFragment(newFragment = newFragment, animType = animType)
    }

    fun openFragment(newFragment: Fragment, backStack: String?, @NavigationObserver.Companion.SlideAnimType animType: Int) {
        addFragment(newFragment = newFragment, backStack = backStack, animType = animType)
    }

    private fun replaceFragment(fragmentId: Int = R.id.container, newFragment: Fragment, backStack: String? = null, addBackStack: Boolean = true, @NavigationObserver.Companion.SlideAnimType animType: Int = NavigationObserver.NONE) {
        //TODO isAdd: false
        commit(true, fragmentId, newFragment, backStack, addBackStack, animType)
    }

    private fun addFragment(fragmentId: Int = R.id.container, newFragment: Fragment, backStack: String? = null, @NavigationObserver.Companion.SlideAnimType animType: Int = NavigationObserver.LEFT_TO_RIGHT) {
        commit(true, fragmentId, newFragment, backStack, true, animType)
    }

    private fun commit(isAdd: Boolean = true, fragmentId: Int, newFragment: Fragment, backStack: String?, addToBackStack: Boolean, @NavigationObserver.Companion.SlideAnimType animType: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        val previousFragment = supportFragmentManager.findFragmentById(fragmentId)
        if (previousFragment != null) {
            // Exit for Previous Fragment
            val exitTransitionSet = TransitionSet()
            // Enter Transition for New Fragment
            val enterTransitionSet = TransitionSet()

            when (animType) {
                NavigationObserver.LEFT_TO_RIGHT -> {
                    exitTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.slide_left))
                    enterTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.slide_right))
                }
                NavigationObserver.BOTTOM_TO_TOP -> {
                    exitTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.slide_top))
                    enterTransitionSet.addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.slide_bottom))
                }
                NavigationObserver.NONE -> {
                }
            }
            previousFragment.exitTransition = exitTransitionSet
            newFragment.enterTransition = enterTransitionSet
        }


        if (isAdd) {
            transaction.add(fragmentId, newFragment)
        } else {
            transaction.replace(fragmentId, newFragment)
        }
        transaction.show(newFragment)
        if (addToBackStack) {
            transaction.addToBackStack(backStack)
        }
        commit(transaction)
    }

    private fun commit(transaction: FragmentTransaction) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            transaction.commit()
            transactionToRecord = null
        } else {
            transactionToRecord = transaction
        }
    }

    companion object {
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @IntDef(value = *longArrayOf(NONE.toLong(), LEFT_TO_RIGHT.toLong(), BOTTOM_TO_TOP.toLong()))
        annotation class SlideAnimType

        const val NONE = 1
        const val LEFT_TO_RIGHT = NONE + 1
        const val BOTTOM_TO_TOP = LEFT_TO_RIGHT + 1
    }
}