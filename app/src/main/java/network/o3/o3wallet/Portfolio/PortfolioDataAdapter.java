package network.o3.o3wallet.Portfolio;
import com.robinhood.spark.SparkAdapter;

/**
 * Created by drei on 11/26/17.
 */

public class PortfolioDataAdapter extends SparkAdapter {
    private float[] yData;

    public PortfolioDataAdapter(float[] yData) {
        this.yData = yData;
    }

    @Override
    public int getCount() {
        return yData.length;
    }

    public void setData(float[] yData) {
        this.yData = yData;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int index) {
        return yData[index];
    }

    @Override
    public float getY(int index) {
        return yData[index];
    }
}
