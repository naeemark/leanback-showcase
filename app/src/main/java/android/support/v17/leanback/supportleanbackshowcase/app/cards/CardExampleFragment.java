/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package android.support.v17.leanback.supportleanbackshowcase.app.cards;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.supportleanbackshowcase.R;
import android.support.v17.leanback.supportleanbackshowcase.app.details.ShadowRowPresenterSelector;
import android.support.v17.leanback.supportleanbackshowcase.cards.presenters.CardPresenterSelector;
import android.support.v17.leanback.supportleanbackshowcase.models.Card;
import android.support.v17.leanback.supportleanbackshowcase.models.CardRow;
import android.support.v17.leanback.supportleanbackshowcase.utils.CardListRow;
import android.support.v17.leanback.supportleanbackshowcase.utils.Utils;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DividerRow;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SectionRow;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * This fragment will be shown when the "Card Examples" card is selected at the home menu. It will
 * display multiple card types.
 */
public class CardExampleFragment extends BrowseFragment {

    private Context mContext;
    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupUi();
        setupRowAdapter();
    }

    private void setupUi() {
        setupHeaders(false);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClicked(Presenter.ViewHolder viewHolder, Object item, RowPresenter.ViewHolder viewHolder1, Row row) {


                if (!(item instanceof Card)) return;
                if (!(viewHolder.view instanceof ImageCardView)) return;


                Toast.makeText(getActivity(), "Clicked: " + ((Card) item).getTitle(), Toast.LENGTH_SHORT).show();

//
//                ImageView imageView = ((ImageCardView) viewHolder.view).getMainImageView();
//                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
//                        imageView, DetailViewExampleFragment.TRANSITION_NAME).toBundle();
//                Intent intent = new Intent(getActivity().getBaseContext(),
//                        DetailViewExampleActivity.class);
//                Card card = (Card) item;
//                int imageResId = card.getLocalImageResourceId(getContext());
//                intent.putExtra(DetailViewExampleFragment.EXTRA_CARD, imageResId);
//                startActivity(intent, bundle);
            }

        });

        prepareEntranceTransition();
    }

    private void setupHeaders(boolean show) {
        if (!show) {
            setHeadersState(HEADERS_DISABLED);
            return;
        }
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setTitle(getString(R.string.card_examples_title));

        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getString(R.string.implement_search),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupRowAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());
        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 500);
    }

    private void createRows() {
        String json = Utils
                .inputStreamToString(getResources().openRawResource(R.raw.cards_example));
        CardRow[] rows = new Gson().fromJson(json, CardRow[].class);
        for (CardRow row : rows) {
            row.useShadow();
            mRowsAdapter.add(createCardRow(row));
        }
    }

    private Row createCardRow(final CardRow cardRow) {
        switch (cardRow.getType()) {
            case CardRow.TYPE_SECTION_HEADER:
                return new SectionRow(new HeaderItem(cardRow.getTitle()));
            case CardRow.TYPE_DIVIDER:
                return new DividerRow();
            case CardRow.TYPE_DEFAULT:
            default:
                // Build main row using the ImageCardViewPresenter.
                PresenterSelector presenterSelector = new CardPresenterSelector(getActivity());
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenterSelector);
                for (Card card : cardRow.getCards()) {
                    listRowAdapter.add(card);
                }

                HeaderItem hi = new HeaderItem(cardRow.getTitle());
                hi.setDescription("This is sample description");
                hi.setContentDescription("This is sample content-description");

                return new CardListRow(hi, listRowAdapter, cardRow);
//                return new CardListRow(null, listRowAdapter, cardRow);
        }
    }

}
