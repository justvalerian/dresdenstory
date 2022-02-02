package com.langepinilla.dresdenstory;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

//The app uses a StoryActivity which in turn displays each chapter as a fragment which is defined here

public class ChapterFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHAPTER_NUMBER = "chapterNumber";

    //chapter number of the fragment instance
    private int mChapterNumber = 0;

    public ChapterFragment() {
        // Required empty public constructor
    }

    //method to create new fragment instance
    public static ChapterFragment newInstance(int chapterNumber) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putInt(CHAPTER_NUMBER, chapterNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //on create use the arguments passed in newInstance to set mChapterNumber
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChapterNumber = getArguments().getInt(CHAPTER_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chapter, container, false);

        //initiate ViewModel
        ChapterViewModel mChapterViewModel = new ViewModelProvider(this).get(ChapterViewModel.class);

        //assign all relevant views to variables
        TextView chapterTitle = v.findViewById(R.id.chapter_title);
        TextView chapterLocation = v.findViewById(R.id.chapter_location);
        TextView chapterShortDescription = v.findViewById(R.id.chapter_short_description);
        TextView chapterContent = v.findViewById(R.id.chapter_content);
        TextView chapterQuestionTitle = v.findViewById(R.id.chapter_question_title);
        TextView chapterQuestion = v.findViewById(R.id.chapter_question);
        LinearLayout chapterAnswerContainer = v.findViewById(R.id.chapter_answer_container);
        EditText chapterAnswer = v.findViewById(R.id.chapter_answer);
        Button chapterSend = v.findViewById(R.id.chapter_send);

        //observe for database changes
        mChapterViewModel.getChapter(mChapterNumber).observe(getViewLifecycleOwner(), chapter -> {
            //set unlocked texts and change layout parts to visible if chapter is unlocked
            if (chapter.isUnlocked()) {
                chapterTitle.setText(chapter.getName());
                chapterLocation.setText(String.format(requireContext().getString(R.string.chapter_location_format_text_lat_lon), chapter.getLocationText(), chapter.getLocationLat(), chapter.getLocationLon()));
                chapterShortDescription.setText(chapter.getShortDescription());
                //for content read HTML tags and set a custom imageGetter
                chapterContent.setText(HtmlCompat.fromHtml(chapter.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY, source -> {
                    //image source needs to be image name without .jpg/.png/...
                    Drawable drawFromPath;
                    int path = requireContext().getResources().getIdentifier(source, "drawable", requireActivity().getPackageName());
                    drawFromPath = ResourcesCompat.getDrawable(getResources(), path, null);
                    if (drawFromPath != null) {
                        drawFromPath.setBounds(0, 0, drawFromPath.getIntrinsicWidth() / 3, drawFromPath.getIntrinsicHeight() / 3);
                    }
                    return drawFromPath;
                }, null));
                chapterQuestion.setText(chapter.getQuestion());

                chapterTitle.setVisibility(View.VISIBLE);
                chapterLocation.setVisibility(View.VISIBLE);
                chapterShortDescription.setVisibility(View.VISIBLE);
                chapterQuestionTitle.setVisibility(View.VISIBLE);
                chapterQuestion.setVisibility(View.VISIBLE);
                chapterAnswerContainer.setVisibility(View.VISIBLE);
            }
        });

        //on click listener for the send button
        chapterSend.setOnClickListener(view -> mChapterViewModel.getAllChapters().observe(getViewLifecycleOwner(), chapters -> {

            Chapter thisChapter = chapters.get(mChapterNumber);
            Chapter nextChapter = null;
            //check if next chapter still exists before getting it, this prevents an ArrayOutOfBounds exception
            if (mChapterNumber + 1 < chapters.size()) {
                nextChapter = chapters.get(mChapterNumber + 1);
            }

            //check if there still is a next chapter
            if (nextChapter == null) {
                Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout),
                        R.string.chapter_feedback_finished,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.chapter_feedback_finished_button, snackView -> {
                        })
                        .show();
            }

            //check if chapter is already unlocked
            else if (nextChapter.isUnlocked()) {
                Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout),
                        R.string.chapter_feedback_already_unlocked,
                        Snackbar.LENGTH_SHORT)
                        .setAction(R.string.chapter_feedback_already_unlocked_button, snackView -> {
                        })
                        .show();
            }

            //only then continue to actually check the answer
            else {
                //initialize relevant variables:

                String userAnswer = chapterAnswer.getText().toString();
                //now do clean up steps to handle accidental wrong answers
                //delete different case from answer
                userAnswer = userAnswer.toLowerCase();
                //delete spaces from answer
                userAnswer = userAnswer.replaceAll(" ", "");
                //delete punctuation symbols from answer
                userAnswer = userAnswer.replaceAll("\\p{Punct}+", "");

                //split answer string from database into a string array
                String[] possibleAnswers = thisChapter.getAnswer().split(",");
                String hint = thisChapter.getHint();

                CHECKALLANSWERS:
                {
                    //check if answer is correct or not: iterate possible answers
                    for (String possibleAnswer : possibleAnswers) {
                        // check if answer equals one of the possible answers
                        if (userAnswer.equals(possibleAnswer)) {
                            mChapterViewModel.updateUnlocked(new ChapterUpdateUnlocked(mChapterNumber + 1, true));

                            //go to unlocked chapter
                            Intent intent = new Intent(requireContext(), StoryActivity.class);
                            intent.putExtra("goToChapter", mChapterNumber + 1);
                            intent.putExtra("newlyUnlocked", true);
                            startActivity(intent);

                            //break out of labelled section so that wrong answer message is not displayed
                            break CHECKALLANSWERS;
                        }
                    }
                    //only do this once after all possible answers were checked
                    Snackbar.make(requireActivity().findViewById(R.id.coordinator_layout),
                            getString(R.string.chapter_feedback_wrong) + hint,
                            Snackbar.LENGTH_LONG)
                            .setAction(R.string.chapter_feedback_wrong_button, snackView -> {
                            })
                            .show();
                }
            }

            //clear edit text field
            chapterAnswer.getText().clear();

            //close keyboard
            Objects.requireNonNull(ViewCompat.getWindowInsetsController(requireView())).hide(WindowInsetsCompat.Type.ime());

        }));

        //listener on the edittext for the send key on the keyboard or the enter key pressed
        chapterAnswer.setOnEditorActionListener((textView, keyCode, keyEvent) -> {
            if (keyCode == EditorInfo.IME_ACTION_GO || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                chapterSend.callOnClick();
                return true;
            }
            return false;
        });

        return v;
    }

}