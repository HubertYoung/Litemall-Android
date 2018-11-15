package com.hubertyoung.component.acfunvideo.index.vm;

import android.app.Application;

import com.hubertyoung.common.base.AbsViewModel;
import com.hubertyoung.component.acfunvideo.index.source.HomePageRepository;

import androidx.annotation.NonNull;

/**
 * <br>
 * function:
 * <p>
 *
 * @author:HubertYoung
 * @date:2018/11/12 11:25
 * @since:V$VERSION
 * @desc:com.hubertyoung.component_acfunmine.ui.sign.vm
 */
public class HomePageViewModel extends AbsViewModel< HomePageRepository > {

	public HomePageViewModel( @NonNull Application application ) {
		super( application );
	}

	public void requestDomainAndroidCfg() {
		mRepository.requestDomainAndroidCfg();
	}
}