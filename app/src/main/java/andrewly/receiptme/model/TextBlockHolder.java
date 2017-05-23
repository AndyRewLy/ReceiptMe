package andrewly.receiptme.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Andrew Ly on 5/22/2017.
 */

public class TextBlockHolder implements Parcelable{

    private TextBlock textBlock;

    private ArrayList<Double> textPrices;
    private ArrayList<String> textItems;

    public TextBlockHolder() {
    }

    public TextBlockHolder(Parcel in) {
        this.textPrices = ((TextBlockHolder) in.readValue(TextBlockHolder.class.getClassLoader())).getTextPrices();
        this.textItems = ((TextBlockHolder) in.readValue(TextBlockHolder.class.getClassLoader())).getTextItems();
    }

    public TextBlockHolder(TextBlock block) {
        this.textBlock = block;
    }

    public TextBlock getTextBlock() {
        return this.textBlock;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TextBlockHolder createFromParcel(Parcel in) {
            return new TextBlockHolder(in);
        }

        public TextBlockHolder[] newArray(int size) {
            return new TextBlockHolder[size];
        }
    };

    public ArrayList<Double> getTextPrices() {
        return textPrices;
    }

    public void setTextPrices(ArrayList<Double> textPrices) {
        this.textPrices = textPrices;
    }

    public ArrayList<String> getTextItems() {
        return textItems;
    }

    public void setTextItems(ArrayList<String> textItems) {
        this.textItems = textItems;
    }
}
