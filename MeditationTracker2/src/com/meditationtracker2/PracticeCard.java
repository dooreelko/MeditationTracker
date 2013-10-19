package com.meditationtracker2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.fima.cardsui.objects.Card;
import com.meditationtracker2.content.ICanFillView;
import com.meditationtracker2.content.data.Practice;

public class PracticeCard extends Card {
	private ICanFillView<Practice> filler;
	private Practice practice;
	
	public PracticeCard(Practice practice, ICanFillView<Practice> filler) {
		super();
		this.filler = filler;
		this.practice = practice;
	}

	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.fragment_practice_intro, null);

		filler.fill(view, practice);
		
		return view;
	}

}
