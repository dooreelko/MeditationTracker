package com.meditationtracker2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import butterknife.Views;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class PictureSourceDialog extends SherlockDialogFragment {
	public interface IChoosePicture {
		int CHOOSE_EXISTING = 0;
		int TAKE_PICTURE = 1;
		
		void onPictureSourceChosen(int result);
	}

	public PictureSourceDialog() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dialog_image_source, container);
		
		Views.findById(view, R.id.btnChoosePicture).setOnClickListener(onBtnClick);
		Views.findById(view, R.id.btnTakePicture).setOnClickListener(onBtnClick);
		
        getDialog().setTitle(R.string.image_source_title);

        return view;
    }

	private OnClickListener onBtnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			((IChoosePicture)getActivity()).onPictureSourceChosen(v.getId() == R.id.btnChoosePicture ? IChoosePicture.CHOOSE_EXISTING : IChoosePicture.TAKE_PICTURE);
			PictureSourceDialog.this.dismiss();
		}
	};
	
}
