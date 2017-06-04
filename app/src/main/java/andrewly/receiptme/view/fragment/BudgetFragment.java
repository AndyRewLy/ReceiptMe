package andrewly.receiptme.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import andrewly.receiptme.R;
import andrewly.receiptme.model.dao.BudgetDao;
import andrewly.receiptme.model.dao.CostDao;

/**
 * Created by Andrew Ly on 6/4/2017.
 */

public class BudgetFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;

    public BudgetFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BudgetFragment newInstance(int sectionNumber) {
        BudgetFragment fragment = new BudgetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        Button setBudget = (Button)rootView.findViewById(R.id.setBudget);

        updateBudgetView();

        setBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText input = (EditText)rootView.findViewById(R.id.inputBudget);
                String inputBudget = input.getText().toString();
                double inputValue = Double.parseDouble(inputBudget);

                BudgetDao.editBudget(inputValue);

                updateBudgetView();
            }
        });

        return rootView;
    }

    public void updateBudgetView() {

        double currentBudget = BudgetDao.getBudget();
        double moneyLeft = currentBudget - CostDao.todaysExpenditures;

        TextView budgetDisplay = (TextView)rootView.findViewById(R.id.showBudget);
        TextView moneyLeftDisplay = (TextView)rootView.findViewById(R.id.usedUpBudget);

        Log.d("SettingBudgetFragment", "Budget Fragment has values for budget and moneyleft" + currentBudget + " " + moneyLeft);
        budgetDisplay.setText("$" + currentBudget);
        moneyLeftDisplay.setText("$" + moneyLeft);
    }
}
