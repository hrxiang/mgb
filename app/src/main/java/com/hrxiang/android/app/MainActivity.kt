package com.hrxiang.android.app

import android.app.Dialog
import com.hrxiang.android.app.presenter.MainPresenter
import com.hrxiang.android.app.presenter.contract.MainContract
import com.hrxiang.android.base.ui.activity.BaseActivity
import com.hrxiang.android.base.widget.DialogHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainContract.IView {

    override fun apiResult(result: String?) {
        content.text = result;
    }

    var presenter: MainPresenter? = null;

    override fun createPresenters() {
//        presenter = MainPresenter().onAttach(this@MainActivity);
        presenter = MainPresenter(this@MainActivity)
    }

    override fun getContentLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initWidgetAndEvent() {
        super.initWidgetAndEvent()
        content.setOnClickListener {
            presenter?.apiTest()
        }
    }

    override fun createLoadingDialog(): Dialog {
        return DialogHelper
            .newBuilder(this)
            .setBackgroundDimEnabled(false)
            .setContentView(R.layout.loading)
            .create();
    }
}
