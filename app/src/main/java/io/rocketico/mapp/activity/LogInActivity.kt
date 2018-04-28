package io.rocketico.mapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import io.rocketico.mapp.fragment.JoinFragment
import io.rocketico.mapp.fragment.LogInFragment

class LogInActivity : AppCompatActivity(),
        LogInFragment.LogInFragmentListener,
        JoinFragment.JoinFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val fragmentId = intent.getIntExtra(FRAGMENT_ID, 0)

        init(fragmentId)
    }

    private fun init(fragmentId: Int) {
        val transaction = supportFragmentManager.beginTransaction()

        if (fragmentId == LOG_IN_FRAGMENT) {
            Utils.setStatusBarColor(this, resources.getColor(R.color.logInButtonColor))
            transaction.replace(R.id.logInContainer, LogInFragment())
        }
        if (fragmentId == JOIN_FRAGMENT) {
            Utils.setStatusBarColor(this, resources.getColor(R.color.joinColor))
            transaction.replace(R.id.logInContainer, JoinFragment())
        }

        transaction.commit()
    }

    override fun onBackClick() {
        onBackPressed()
    }

    companion object {
        private const val FRAGMENT_ID = "fragment_id"
        const val LOG_IN_FRAGMENT = 0
        const val JOIN_FRAGMENT = 1

        fun newIntent(context: Context, fragmentId: Int): Intent {
            val intent = Intent(context, LogInActivity::class.java)
            intent.putExtra(FRAGMENT_ID, fragmentId)
            return intent
        }
    }
}
