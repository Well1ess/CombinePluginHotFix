package zlh.com.combinepluginhotfix;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PluginOneMainFragment extends Fragment {

    public PluginOneMainFragment() {
    }

    public static PluginOneMainFragment newInstance() {
        PluginOneMainFragment fragment = new PluginOneMainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return new View(getContext());
    }

}
