package com.example.skincare.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.skincare.R;

public class SkinQuizFragment extends Fragment {

    private RadioGroup rg_question1, rg_question2, rg_question3, rg_question4,
            rg_question5, rg_question6, rg_question7;
    private Button btn_submit;

    // Scores as class fields
    private int dry = 0, oily = 0, normal = 0, sensitive = 0;

    public SkinQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_skin_quiz, container, false);

        // Initialize RadioGroups
        rg_question1 = view.findViewById(R.id.rg_question1);
        rg_question2 = view.findViewById(R.id.rg_question2);
        rg_question3 = view.findViewById(R.id.rg_question3);
        rg_question4 = view.findViewById(R.id.rg_question4);
        rg_question5 = view.findViewById(R.id.rg_question5);
        rg_question6 = view.findViewById(R.id.rg_question6);
        rg_question7 = view.findViewById(R.id.rg_question7);

        // Initialize Submit button
        btn_submit = view.findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(v -> {
            calculateSkinType();
            clearAllSelections();
        });

        return view;
    }

    private void calculateSkinType() {
        // Reset scores each time
        dry = 0; oily = 0; normal = 0; sensitive = 0;

        scoreQuestion(rg_question1, "Tight", "Normal", "Oily", "Sensitive");
        scoreQuestion(rg_question2, "Often", "Sometimes", "Rarely", "Normal");
        scoreQuestion(rg_question3, "Very", "Moderate", "Low", "Normal");
        scoreQuestion(rg_question4, "Burns easily", "Tans easily", "Both", "Normal");
        scoreQuestion(rg_question5, "Very shiny", "Slightly shiny", "Not shiny", "Normal");
        scoreQuestion(rg_question6, "Often", "Sometimes", "Rarely", "Normal");
        scoreQuestion(rg_question7, "Often", "Sometimes", "Rarely", "Normal");

        // Determine skin type
        int maxScore = Math.max(Math.max(dry, oily), Math.max(normal, sensitive));
        String skinType;
        if (maxScore == dry) skinType = "Dry";
        else if (maxScore == oily) skinType = "Oily";
        else if (maxScore == sensitive) skinType = "Sensitive";
        else skinType = "Normal";

        Toast.makeText(getContext(), "Your skin type is: " + skinType, Toast.LENGTH_LONG).show();
    }

    private void scoreQuestion(RadioGroup rg, String dryOpt, String normalOpt, String oilyOpt, String sensitiveOpt) {
        int selectedId = rg.getCheckedRadioButtonId();
        if (selectedId == -1) return;

        RadioButton selected = getView().findViewById(selectedId);
        String answer = selected.getText().toString();

        if (answer.equals(dryOpt)) dry += 2;
        else if (answer.equals(normalOpt)) normal += 2;
        else if (answer.equals(oilyOpt)) oily += 2;
        else if (answer.equals(sensitiveOpt)) sensitive += 2;
    }

    private void clearAllSelections() {
        rg_question1.clearCheck();
        rg_question2.clearCheck();
        rg_question3.clearCheck();
        rg_question4.clearCheck();
        rg_question5.clearCheck();
        rg_question6.clearCheck();
        rg_question7.clearCheck();
    }
}
