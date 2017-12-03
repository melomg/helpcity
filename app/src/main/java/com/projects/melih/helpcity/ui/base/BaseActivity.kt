package com.projects.melih.helpcity.ui.base

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View

/**
 * Created by melih on 1.12.2017.
 */
abstract class BaseActivity : AppCompatActivity(), NavigationListener {

    protected lateinit var snackView: View
    private lateinit var navigationObserver: NavigationObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationObserver = NavigationObserver(lifecycle, supportFragmentManager, this)
        lifecycle.addObserver(navigationObserver)
    }

    override fun openFragment(newFragment: Fragment, backStack: String?, animType: Int) {
        navigationObserver.openFragment(newFragment, backStack, animType)
    }

    override fun openFragmentAndClearStack(newFragment: Fragment, animType: Int) {
        navigationObserver.openFragmentAndClearStack(newFragment, animType)
    }

    override fun initToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.finish()
        }
    }

    protected fun clearBackStack() {
        navigationObserver.clearBackStack()
    }

    protected fun showSnackBar(message: String) {
        Snackbar.make(snackView, message, Snackbar.LENGTH_LONG).show()
    }

    protected fun showSnackBar(message: String, actionMessage: String, listener: View.OnClickListener) {
        Snackbar.make(snackView, message, Snackbar.LENGTH_LONG).setAction(actionMessage, listener).show()
    }
}