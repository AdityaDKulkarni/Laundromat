package com.laundry.laundry.ui;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.laundry.laundry.NFC.NFCHandler;
import com.laundry.laundry.R;
import com.laundry.laundry.adapters.BagRecyclerAdater;
import com.laundry.laundry.listener.RecyclerViewListener;
import com.laundry.laundry.models.BagModel;
import com.laundry.laundry.retrofit.API;
import com.laundry.laundry.retrofit.RetrofitConfig;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private NFCHandler nfcHandler;
    private String TAG = getClass().getSimpleName();
    private Tag tag;
    private EditText etNfcId;
    private Parcelable[] parcelables;
    private FloatingActionButton fabAddBag;
    private RecyclerView rvBag;
    private View addBagView;
    private String tagID, service;
    private AlertDialog alertDialog;
    private Spinner spnService;
    private RetrofitConfig retrofitConfig;
    private ArrayList<BagModel> bagModels;
    private int customerPk, flag = 0, i;
    private ProgressDialog progressDialog;
    private ArrayList<String> tagList;
    private boolean creatable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getString(R.string.bags));


        initUI();

        if (!nfcHandler.isNfcEnabled()) {
            finish();
        }

        createBag();
        getbags();
    }

    private void getbags() {
        progressDialog.show();
        Retrofit retrofit = retrofitConfig.config();
        API api = retrofit.create(API.class);
        Call<List<BagModel>> call = api.getBags();

        call.enqueue(new Callback<List<BagModel>>() {
            @Override
            public void onResponse(Call<List<BagModel>> call, Response<List<BagModel>> response) {
                try {
                    bagModels.clear();
                    if(response.body().size() > 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            BagModel bagModel = new BagModel();
                            bagModel.setPk(response.body().get(i).getPk());
                            bagModel.setUid(response.body().get(i).getUid());
                            bagModel.setCount(response.body().get(i).getCount());
                            bagModel.setService_type(response.body().get(i).getService_type());
                            customerPk = response.body().get(i).getCustomerModel();
                            bagModel.setCustomerModel(response.body().get(i).getCustomerModel());
                            bagModels.add(bagModel);
                        }

                        rvBag.addOnItemTouchListener(new RecyclerViewListener(MainActivity.this, rvBag, new RecyclerViewListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this, ClothActivity.class);
                                intent.putExtra("bagPk", bagModels.get(position).getPk());
                                intent.putExtra("customerPk", bagModels.get(position).getCustomerModel());
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }));
                    }else{
                        creatable = true;
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    rvBag.setAdapter(new BagRecyclerAdater(MainActivity.this, bagModels));
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    creatable = true;
                    Toast.makeText(MainActivity.this, getString(R.string.no_bags), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BagModel>> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(MainActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createBag() {
        fabAddBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(creatable) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    if (addBagView.getParent() != null) {
                        ((ViewGroup) addBagView.getParent()).removeView(addBagView);
                    }
                    builder.setView(addBagView);
                    if (tagID != null) {
                        etNfcId.setText(tagID);
                    }
                    builder.setPositiveButton(getString(R.string.create), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                if (tag == null) {
                                    Toast.makeText(MainActivity.this, getString(R.string.no_tag), Toast.LENGTH_SHORT).show();
                                } else if (spnService.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.service_default))) {
                                    Toast.makeText(MainActivity.this, getString(R.string.service_default), Toast.LENGTH_SHORT).show();
                                } else {
                                    if (spnService.getSelectedItem().toString().equalsIgnoreCase("wash")) {
                                        service = "wash";
                                    } else if (spnService.getSelectedItem().toString().equalsIgnoreCase("iron")) {
                                        service = "iron";
                                    } else if (spnService.getSelectedItem().toString().equalsIgnoreCase("dry clean")) {
                                        service = "dry_clean";
                                    }
                                    Retrofit retrofit = new RetrofitConfig().config();
                                    API api = retrofit.create(API.class);
                                    Call<ResponseBody> call = api.createBags(2, 0,
                                            tagID,
                                            "received",
                                            service);

                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Toast.makeText(MainActivity.this, "Bag added", Toast.LENGTH_SHORT).show();
                                            getbags();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                etNfcId.setText("");
                            }
                        }
                    });
                    builder.setCancelable(false);
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            etNfcId.setText("");
                        }
                    });

                    alertDialog = builder.create();
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dismiss),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    etNfcId.setText("");
                                    if (alertDialog.isShowing()) {
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                    if (!alertDialog.isShowing()) {
                        alertDialog.show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.bag_already_present), Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    private void initUI() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.getting_bags));
        progressDialog.setCancelable(false);
        retrofitConfig = new RetrofitConfig();
        addBagView = LayoutInflater.from(this).inflate(R.layout.layout_add_bag, null);
        etNfcId = addBagView.findViewById(R.id.etNfcTagId);
        spnService = addBagView.findViewById(R.id.spnService);
        fabAddBag = findViewById(R.id.fbAddBag);
        rvBag = findViewById(R.id.rvBag);
        rvBag.setLayoutManager(new LinearLayoutManager(this));
        nfcHandler = new NFCHandler(NfcAdapter.getDefaultAdapter(MainActivity.this),
                MainActivity.this);
        tagList = new ArrayList();
        bagModels = new ArrayList();
        ArrayList<String> strings = new ArrayList();
        strings.add(getString(R.string.service_default));
        strings.add(getString(R.string.service_wash));
        strings.add(getString(R.string.service_dry_clean));
        strings.add(getString(R.string.service_iron));

        spnService.setAdapter(new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_list_item_1, strings));
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagFilter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter techFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        IntentFilter[] filters = new IntentFilter[]{tagFilter, techFilter, ndefFilter};
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (nfcHandler.getNfcAdapter() != null) {
            nfcHandler.getNfcAdapter().enableForegroundDispatch(this, pendingIntent, filters, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcHandler.getNfcAdapter() != null) {
            nfcHandler.getNfcAdapter().disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        Log.d(TAG, intent.getAction());
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            try {
                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                tagID = nfcHandler.getNfcId(tag.getId());
                for(i = 0; i < bagModels.size(); i++){
                    if(bagModels.get(i).getUid().equalsIgnoreCase(tagID)){
                        break;
                    }else{
                        flag = 1;
                    }
                }

                if((i == bagModels.size() && flag == 1) || bagModels.isEmpty()){
                    creatable = true;
                    Toast.makeText(MainActivity.this, getString(R.string.new_tag), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.bag_already_present), Toast.LENGTH_LONG).show();
                    creatable = false;
                }
                etNfcId.setText(tagID);
                parcelables = intent.getParcelableArrayExtra(nfcHandler.getNfcAdapter().EXTRA_NDEF_MESSAGES);
                if (tag != null) {
                    Log.d(TAG, getString(R.string.tag_detected));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
