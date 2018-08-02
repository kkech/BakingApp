package com.example.android.bakingapp.view.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.bakingapp.DetailsActivity;
import com.example.android.bakingapp.model.dto.DetailRecipe;
import com.example.android.bakingapp.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bakingapp.DetailsActivity.twoPane;

public class RecipeStepFragment extends Fragment implements View.OnClickListener, ExoPlayer.EventListener {

    private static final String LOG_TAG = RecipeStepFragment.class.getSimpleName();

    @BindView(R.id.instructions_step_recipe)
    TextView instructions;

    @BindView(R.id.no_video_found)
    ImageView noVideoImageView;

    @BindView(R.id.previous_section)
    Button previousStep;

    @BindView(R.id.next_section)
    Button nextStep;

    @BindView(R.id.footer_buttons)
    LinearLayout footerButtons;

    @BindView(R.id.main_content)
    RelativeLayout mainView;

    @BindView(R.id.details_view)
    LinearLayout detailView;

    SimpleExoPlayer mExoPlayer;

    @BindView(R.id.video_recipe_step)
    SimpleExoPlayerView mPlayerView;

    private static MediaSessionCompat mMediaSession;

    private PlaybackStateCompat.Builder mStateBuilder;

    private static final String RECIPE_NO_VIDEO = "No video";
    private static final String RECIPE_STEPS_EXTRA = "Recipe Steps";
    private static final String RECIPE_STEPS_POSITION_EXTRA = "Recipe Step Position";

    private ArrayList<DetailRecipe> detailRecipes;
    private int positionStep;
    private DetailRecipe detailRecipe;
    private boolean no_video;

    public static boolean changed = false;

    private RecipeStepFragment.onRecipeNextPreviousSelected mCallback;

    public interface onRecipeNextPreviousSelected {
        void onRecipeNextPreviousSelected(int position, ArrayList<DetailRecipe> detailRecipes);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            detailRecipes = bundle.getParcelableArrayList(RECIPE_STEPS_EXTRA);
            positionStep = bundle.getInt(RECIPE_STEPS_POSITION_EXTRA);
        }
        setRetainInstance(true);
        ((DetailsActivity) getActivity()).setActionBarTitle(detailRecipes.get(positionStep).getDetailTitle());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        ButterKnife.bind(this, view);

        if (twoPane) {
            footerButtons.setVisibility(View.GONE);
        }
        //get recipe step details
        detailRecipe = detailRecipes.get(positionStep);

        populateButtons();
        instructions.setText(detailRecipe.getDetailInstructions());

        if (changed) {
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            mainView.setPadding(0, ((DetailsActivity) getActivity()).getActionBarHeight() + 4 * padding, 0, 0);
        }

        changeLayoutOrientation();

        if (savedInstanceState != null) {
            no_video = savedInstanceState.getBoolean(RECIPE_NO_VIDEO);
            if(!no_video) {
                mPlayerView.setPlayer(mExoPlayer);
                noVideoImageView.setVisibility(View.GONE);
            }else{
                mPlayerView.setVisibility(View.GONE);
                noVideoImageView.setVisibility(View.VISIBLE);
                mainView.setPadding(0,0,0,0);
            }
            return view;
        }

        if (!detailRecipe.getDetailVideo().isEmpty() || !detailRecipe.getDetailVideo().equals("")) {
            no_video = false;
            //initialize Media Session.
            initializeMediaSession();
            //initialize player.
            initializePlayer(Uri.parse(detailRecipe.getDetailVideo()));
            noVideoImageView.setVisibility(View.GONE);
        } else {
            no_video = true;
            mPlayerView.setVisibility(View.GONE);
            noVideoImageView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void initializeMediaSession() {

        //create a MediaSessionCompat
        mMediaSession = new MediaSessionCompat(getContext(), LOG_TAG);

        //enable callbacks
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //don't restart
        mMediaSession.setMediaButtonReceiver(null);

        //set an initial PlaybackState
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        //MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        //start the Media Session
        mMediaSession.setActive(true);

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            //create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);

            //prepare MediaSource for .mp4
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void populateButtons() {
        if (positionStep == 0) {
            previousStep.setVisibility(View.GONE);
            nextStep.setText(detailRecipes.get(positionStep + 1).getDetailTitle());
            nextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onRecipeNextPreviousSelected(positionStep + 1, detailRecipes);
                }
            });
        } else if (positionStep == detailRecipes.size() - 1) {
            nextStep.setVisibility(View.GONE);
            previousStep.setText(detailRecipes.get(positionStep - 1).getDetailTitle());
            previousStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onRecipeNextPreviousSelected(positionStep - 1, detailRecipes);
                }
            });
        } else {
            previousStep.setText(detailRecipes.get(positionStep - 1).getDetailTitle());
            previousStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onRecipeNextPreviousSelected(positionStep - 1, detailRecipes);
                }
            });
            nextStep.setText(detailRecipes.get(positionStep + 1).getDetailTitle());
            nextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onRecipeNextPreviousSelected(positionStep + 1, detailRecipes);
                }
            });
        }
    }

    private void changeLayoutOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        if (!no_video) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                changed = true;
                footerButtons.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mPlayerView.setLayoutParams(layoutParams);
                detailView.setPadding(0, 0, 0, 0);
                mainView.setPadding(0, 0, 0, 0);
                ((DetailsActivity) getActivity()).hideActionBar();
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                footerButtons.setVisibility(View.VISIBLE);
                ((DetailsActivity) getActivity()).showActionBar();
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                mPlayerView.setLayoutParams(layoutParams);
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                detailView.setPadding(padding, padding, padding, 0);
                mainView.setPadding(0, ((DetailsActivity) getActivity()).getActionBarHeight() + 3 * padding, 0, 0);
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((DetailsActivity) getActivity()).setActionBarTitle(getContext().getResources().getString(R.string.details_activity));
        if (!getActivity().isChangingConfigurations()) {
            if (!detailRecipe.getDetailVideo().isEmpty() || !detailRecipe.getDetailVideo().equals("")) {
                releasePlayer();
                mMediaSession.setActive(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DetailsActivity) getActivity()).setActionBarTitle(detailRecipes.get(positionStep).getDetailTitle());
        if (no_video && changed) {
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            mainView.setPadding(0, ((DetailsActivity) getActivity()).getActionBarHeight() + 3 * padding, 0, 0);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onRecipeNextPreviousSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onRecipeNextPreviousSelected");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ((DetailsActivity) getActivity()).setActionBarTitle(getContext().getResources().getString(R.string.details_activity));
        if (!getActivity().isChangingConfigurations()) {
            if (!detailRecipe.getDetailVideo().isEmpty() || !detailRecipe.getDetailVideo().equals("")) {
                releasePlayer();
                mMediaSession.setActive(false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RECIPE_NO_VIDEO,no_video);
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
