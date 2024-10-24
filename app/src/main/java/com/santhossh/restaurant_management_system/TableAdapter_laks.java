package com.santhossh.restaurant_management_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableAdapter_laks extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> tableList;
    private ArrayList<String> waiterList;
    private Map<String, ArrayList<Long>> waiterAssignments;  // From Firestore
    private Map<String, ArrayList<Integer>> selectedWaiterAssignments = new HashMap<>();
    private static final String TAG = "TableAdapter";

    public TableAdapter_laks(@NonNull Context context, ArrayList<String> tableList, ArrayList<String> waiterList, Map<String, ArrayList<Long>> waiterAssignments) {
        super(context, 0, tableList);
        this.context = context;
        this.tableList = tableList;
        this.waiterList = waiterList;
        this.waiterAssignments = waiterAssignments;  // Initialize with Firestore data
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_table_laks, parent, false);
        }

        // Get the table name
        String tableName = tableList.get(position);
        final int tableNumber = position + 1;  // Table numbers are 1-indexed

        // Set the table name in the TextView
        TextView tableTextView = convertView.findViewById(R.id.tableName);
        tableTextView.setText(tableName);

        // Set up the waiter spinner
        Spinner waiterSpinner = convertView.findViewById(R.id.waiterSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, waiterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waiterSpinner.setAdapter(adapter);

        // Pre-select the correct waiter based on saved assignments
        for (Map.Entry<String, ArrayList<Long>> entry : waiterAssignments.entrySet()) {
            String waiter = entry.getKey();
            ArrayList<Long> assignedTables = entry.getValue();

            if (assignedTables.contains((long) tableNumber)) {
                int spinnerPosition = waiterList.indexOf(waiter);
                waiterSpinner.setSelection(spinnerPosition);  // Set spinner to correct waiter
                break;
            }
        }

        // Listener to capture selected waiter
        waiterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedWaiter = waiterList.get(position);

                // Remove this table from all previous waiters' lists
                for (Map.Entry<String, ArrayList<Integer>> entry : selectedWaiterAssignments.entrySet()) {
                    entry.getValue().remove(Integer.valueOf(tableNumber));
                }

                // Add the table to the selected waiter's list
                ArrayList<Integer> assignedTables = selectedWaiterAssignments.getOrDefault(selectedWaiter, new ArrayList<>());
                if (!assignedTables.contains(tableNumber)) {
                    assignedTables.add(tableNumber);
                    selectedWaiterAssignments.put(selectedWaiter, assignedTables);
                }

                Log.d(TAG, "Assigned " + selectedWaiter + " to Table " + tableNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle no selection case
            }
        });

        return convertView;
    }

    // Method to get the waiter assignments for saving
    public Map<String, ArrayList<Integer>> getWaiterAssignments() {
        return selectedWaiterAssignments;
    }
}
