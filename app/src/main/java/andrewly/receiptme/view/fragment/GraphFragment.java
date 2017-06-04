package andrewly.receiptme.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import andrewly.receiptme.R;
import andrewly.receiptme.model.Today;
import andrewly.receiptme.model.dao.CostDao;

import static andrewly.receiptme.R.id.plot;
import static android.content.ContentValues.TAG;

/**
 * Created by Andrew Ly on 5/23/2017.
 */

public class GraphFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private Button category;
    private String selectedCategory;

    private Today currentDay;

    private Date[] pastSevenDays;
    private Number[] costData;

    private XYPlot plot1;
    private XYSeries xySeries;

    public GraphFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GraphFragment newInstance(int sectionNumber) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;

        rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        category = (Button)rootView.findViewById(R.id.category_button);
        plot1 = (XYPlot) rootView.findViewById(plot);

        //Initializing today
        currentDay = Today.getInstance();

        pastSevenDays = new Date[7];
        currentDay.getPastSevenDays(pastSevenDays);

        costData = new Number[7];
        CostDao.getExpenditures(costData);

        //Create Series
        initializeSeries();


        //Onclick Listener for Caetegory
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), category);

                popup.getMenuInflater().inflate(R.menu.menu_category, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        selectedCategory = item.toString();

                        if (selectedCategory != null) {
                            //Obtain the category cost information and relay it to the users some how
                        }


                        return true;
                    }
                });

                popup.show();
            }
        });

        LineAndPointFormatter series1Format = new LineAndPointFormatter(getActivity(), R.xml.line_point_formatter);

        /*plot1.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("0.0"));*/

        plot1.addSeries(xySeries, series1Format);

        //plot1.addSeries(ySeries, series1Format);


        plot1.setRangeBoundaries(0, 10000, BoundaryMode.FIXED);
        plot1.setDomainBoundaries(pastSevenDays[0].getTime(), pastSevenDays[6].getTime(), BoundaryMode.FIXED);
        plot1.setDomainStep(StepMode.SUBDIVIDE, 7);

        plot1.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new Format() {

                    // create a simple date format that draws on the year portion of our timestamp.
                    // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
                    // for a full description of SimpleDateFormat.
                    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");

                    @Override
                    public StringBuffer format(Object obj,
                                               @NonNull StringBuffer toAppendTo,
                                               @NonNull FieldPosition pos) {

                        long i = ((Number) obj).longValue();
                        Log.d(TAG, "formatting date=" + new SimpleDateFormat("MMMM dd, yyyy").format(i));
                        return dateFormat.format(i, toAppendTo, pos);
                    }

                    @Override
                    public Object parseObject(String source, @NonNull ParsePosition pos) {
                        return null;

                    }
                });

        return rootView;
    }

    private void initializeSeries() {
        ArrayList<Number> dateAsLong = new ArrayList<>(7);
        ArrayList<Number> costDataArrList = new ArrayList<>(7);

        for (int i = 0; i < 7; i++) {
            dateAsLong.add(pastSevenDays[i].getTime());
        }

        for (int i = 0; i < 7; i++) {
            costDataArrList.add(costData[i]);
        }

        xySeries = new SimpleXYSeries(dateAsLong, costDataArrList, "Costs vs Date");

    }
}
