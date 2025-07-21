package com.example.internshipapp;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.*;
import com.google.firebase.firestore.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InternshipListFragment extends Fragment {
    private InternshipAdapter adapter;
    private final List<Internship> internshipList = new ArrayList<>();
    private Spinner spinnerFilter, spinnerSort;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internship_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewInternships);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        spinnerSort = view.findViewById(R.id.spinnerSort);

        adapter = new InternshipAdapter(internshipList, internship -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", internship.getTitle());
            bundle.putString("company", internship.getCompany());
            bundle.putString("location", internship.getLocation());
            bundle.putString("duration", internship.getDuration());
            bundle.putString("field", internship.getField());
            bundle.putString("datePosted", internship.getDatePosted());
            bundle.putString("description", internship.getDescription());
            bundle.putString("recruiterId", internship.getRecruiterId());
            Navigation.findNavController(view).navigate(R.id.action_internshipListFragment_to_internshipDetailFragment, bundle);
        });
        recyclerView.setAdapter(adapter);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getContext(), R.array.fields, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        AdapterView.OnItemSelectedListener reloadListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadInternships();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerFilter.setOnItemSelectedListener(reloadListener);
        spinnerSort.setOnItemSelectedListener(reloadListener);

        loadInternships();
        return view;
    }

    private void loadInternships() {
        FirebaseFirestore.getInstance()
                .collection("internships")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    internshipList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Internship internship = doc.toObject(Internship.class);

                        String selectedField = spinnerFilter.getSelectedItem().toString();
                        if (!selectedField.equals("All") && !internship.getField().equalsIgnoreCase(selectedField)) continue;

                        internshipList.add(internship);
                    }

                    String sortOption = spinnerSort.getSelectedItem().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    if (sortOption.equals("Date (Newest First)")) {
                        internshipList.sort((a, b) -> {
                            try {
                                Date dateA = sdf.parse(a.getDatePosted());
                                Date dateB = sdf.parse(b.getDatePosted());
                                assert dateB != null;
                                return dateB.compareTo(dateA);
                            } catch (ParseException e) {
                                return 0;
                            }
                        });
                    } else if(sortOption.equals("Date (Oldest First)")) {
                        internshipList.sort((a, b) -> {
                            try {
                                Date dateA = sdf.parse(a.getDatePosted());
                                Date dateB = sdf.parse(b.getDatePosted());
                                assert dateA != null;
                                return dateA.compareTo(dateB);
                            } catch (ParseException e) {
                                return 0;
                            }
                        });
                    }

                    adapter.setData(internshipList);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load internships", Toast.LENGTH_SHORT).show());
    }

}
