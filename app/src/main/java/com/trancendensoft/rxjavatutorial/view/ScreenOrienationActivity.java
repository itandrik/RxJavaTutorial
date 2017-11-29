package com.trancendensoft.rxjavatutorial.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trancendensoft.rxjavatutorial.R;
import com.trancendensoft.rxjavatutorial.presenter.PresenterManager;
import com.trancendensoft.rxjavatutorial.presenter.RxPresenterImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScreenOrienationActivity extends AppCompatActivity implements ScreenOrientationView {
    @BindView(R.id.btnDoBackground) Button mBtnDoBackground;
    @BindView(R.id.tvResult) TextView mTvResult;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    private RxPresenterImpl mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_orienation);
        ButterKnife.bind(this, this);

        initPresenter(savedInstanceState);
    }

    private void initPresenter(Bundle savedInstanceState) {
        if(savedInstanceState == null){
            mPresenter = new RxPresenterImpl();
        } else {
            mPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPresenter != null){
            mPresenter.bindView(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPresenter != null){
            mPresenter.unbindView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null){
            mPresenter.disposeObservables();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(mPresenter, outState);
    }

    @OnClick(R.id.btnDoBackground)
    protected void onDoOnBackgroundClicked(){
        if(mPresenter != null){
            mPresenter.loadData();
        }
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showServerError() {
        Toast.makeText(this, "Server is not available.", Toast.LENGTH_SHORT).show();
        showContent();
    }

    @Override
    public void showContent() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNetworkError() {
        showContent();
        Toast.makeText(this, "Internet is not available.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showValue(String value) {
        mTvResult.setText(value);
    }
}
