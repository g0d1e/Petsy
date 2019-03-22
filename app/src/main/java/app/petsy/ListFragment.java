package app.petsy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


public class ListFragment extends Fragment {
    private static final String ARG_TYPE = "ARG_TYPE";

    private int type;

    private static int TYPE_FOUND = 0;
    private static int TYPE_LOST = 1;

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private CollectionReference collectionRef;
    private MyAdapter myAdapter;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(int type) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lost, container, false);
        recyclerView = view.findViewById(R.id.ms_Pictures);

        setCollectionRef();
        setRecyclerView();
        getData();

        // Inflate the layout for this fragment
        return view;
    }

    private void setCollectionRef() {
        String collectionName = "";
        if (type == TYPE_FOUND) {
            collectionName = "foundPets";
        } else if (type == TYPE_LOST) {
            collectionName = "lostPets";
        }

        collectionRef = FirebaseFirestore.getInstance().collection(collectionName);
    }

    private void getData() {
                collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                assert snapshots != null;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            PetModel pm = dc.getDocument().toObject(PetModel.class);
                            myAdapter.add(pm);
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }

            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}