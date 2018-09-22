package com.laundry.laundry.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.laundry.laundry.R;
import com.laundry.laundry.adapters.ClothRecyclerAdapter;
import com.laundry.laundry.models.BagModel;
import com.laundry.laundry.models.ClothModel;
import com.laundry.laundry.models.CustomerModel;
import com.laundry.laundry.models.RFIDModel;
import com.laundry.laundry.retrofit.API;
import com.laundry.laundry.retrofit.RetrofitConfig;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ClothActivity extends AppCompatActivity {

    private RecyclerView rvClothes;
    private Button btnCloth, btnUpdateStatus;
    private EditText etNfcTgId, etClothColor;
    private Spinner spnClothType;
    private String tagID;
    private BagModel bagModel;
    private CustomerModel customerModel;
    private int bagPk, customerPk;
    private ArrayList<ClothModel> clothModels;
    private String TAG = getClass().getSimpleName();
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;
    private RFIDModel rfidModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothe);
        getSupportActionBar().setTitle(getString(R.string.clothes));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("bagPk") && getIntent().hasExtra("customerPk")){
            bagPk = getIntent().getIntExtra("bagPk", -1);
            customerPk = getIntent().getIntExtra("customerPk", -1);
        }
        initui();
        getBag();
        getCustomer();
        addCloth();
    }

    private void addCloth() {

        getRFIDTag();

        btnCloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(ClothActivity.this).inflate(R.layout.layout_add_cloth, null);
                etClothColor = view1.findViewById(R.id.etClothColor);
                spnClothType = view1.findViewById(R.id.etClothType);
                ArrayList<String> spnList = new ArrayList();
                spnList.add("Shirt");
                spnList.add("Saree");
                spnList.add("Pant");
                spnList.add("Dress");
                spnList.add("T-shirt");
                spnList.add("Jeans");

                spnClothType.setAdapter(new ArrayAdapter(ClothActivity.this, android.R.layout.simple_list_item_1
                    ,spnList));

                etNfcTgId = view1.findViewById(R.id.etNfcTagId);
                etNfcTgId.setText(tagID);
                AlertDialog.Builder builder = new AlertDialog.Builder(ClothActivity.this);
                if (view1.getParent() != null) {
                    ((ViewGroup) view1.getParent()).removeView(view1);
                }
                builder.setView(view1);
                builder.setPositiveButton(getString(R.string.add_cloth), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            if(etNfcTgId.getText().toString().isEmpty()){
                                etNfcTgId.setError(getString(R.string.cannot_be_empty));
                            } else if(etClothColor.getText().toString().isEmpty()){
                                etClothColor.setError(getString(R.string.cannot_be_empty));
                            }else if(spnClothType.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.select_cloth_default))){
                                Toast.makeText(ClothActivity.this, getString(R.string.select_cloth_default), Toast.LENGTH_SHORT).show();
                            }else {
                                Retrofit retrofit = new RetrofitConfig().config();
                                API api = retrofit.create(API.class);
                                Call<ResponseBody> call = api.createCloth(etNfcTgId.getText().toString(), spnClothType.getSelectedItem().toString().toLowerCase(),
                                        etClothColor.getText().toString(), bagPk);

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Toast.makeText(ClothActivity.this, R.string.cloth_added, Toast.LENGTH_SHORT).show();
                                        getClothes();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            etNfcTgId.setText("");
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        etNfcTgId.setText("");
                    }
                });

                alertDialog = builder.create();
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dismiss),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                etNfcTgId.setText("");
                                if (alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                }
                            }
                        });
                if (!alertDialog.isShowing()) {
                    alertDialog.show();
                }
            }
        });
    }

    private void initui() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.getting_clothes));
        progressDialog.setCancelable(false);
        rvClothes = findViewById(R.id.rvClothes);
        rvClothes.setLayoutManager(new LinearLayoutManager(this));
        btnCloth = findViewById(R.id.btnAddCloth);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(ClothActivity.this, btnUpdateStatus);
                popup.getMenuInflater()
                        .inflate(R.menu.popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.toString().equalsIgnoreCase("received") || item.toString().equalsIgnoreCase("completed")){
                            bagModel.setCurrent_status(item.toString().toLowerCase());
                        }else if(item.toString().equalsIgnoreCase("in progress")){
                            bagModel.setCurrent_status("in_progress");
                        }
                        progressDialog.setMessage(getString(R.string.updating_status));
                        progressDialog.show();
                        Retrofit retrofit = new RetrofitConfig().config();
                        API api = retrofit.create(API.class);

                        Call<BagModel> bagModelCall = api.updateStatus(bagPk, bagModel);

                        bagModelCall.enqueue(new Callback<BagModel>() {
                            @Override
                            public void onResponse(Call<BagModel> call, Response<BagModel> response) {
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<BagModel> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(ClothActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                            }
                        });
                        return true;
                    }
                });

                popup.show();
            }
        });
        clothModels = new ArrayList();
    }


    public void getClothes() {
        Retrofit retrofit = new RetrofitConfig().config();
        API api = retrofit.create(API.class);
        Call<List<ClothModel>> call = api.getClothes();

        call.enqueue(new Callback<List<ClothModel>>() {
            @Override
            public void onResponse(Call<List<ClothModel>> call, Response<List<ClothModel>> response) {
                clothModels.clear();
                try{
                    for(int i = 0; i < response.body().size(); i++){
                        ClothModel clothModel = new ClothModel();
                        if(response.body().get(i).getBag() == bagPk){
                            clothModel.setPk(response.body().get(i).getPk());
                            clothModel.setCloth_type(response.body().get(i).getCloth_type());
                            clothModel.setColor(response.body().get(i).getColor());
                            clothModel.setUid(response.body().get(i).getUid());
                            clothModel.setBag(response.body().get(i).getBag());
                            clothModel.setBagTagId(bagModel.getUid());
                            Log.d("Bag model(response)", String.valueOf(response.body().get(i).getBag()));
                            clothModels.add(clothModel);
                        }
                    }
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    rvClothes.setAdapter(new ClothRecyclerAdapter(ClothActivity.this, clothModels, customerModel.getFirst_name() + customerModel.getLast_name()));
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ClothActivity.this, getString(R.string.no_clothes), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ClothModel>> call, Throwable t) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(ClothActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBag(){
        progressDialog.show();
        Retrofit retrofit = new RetrofitConfig().config();
        API api = retrofit.create(API.class);
        Call<BagModel> call = api.getBag(bagPk);

        call.enqueue(new Callback<BagModel>() {
            @Override
            public void onResponse(Call<BagModel> call, Response<BagModel> response) {
                bagModel = new BagModel();
                bagModel.setPk(response.body().getPk());
                bagModel.setUid(response.body().getUid());
                bagModel.setCustomerModel(response.body().getCustomerModel());
                bagModel.setService_type(response.body().getService_type());
                bagModel.setCurrent_status(response.body().getCurrent_status());
                bagModel.setCompleted(response.body().isCompleted());
                bagModel.setCount(response.body().getCount());
                getClothes();
            }

            @Override
            public void onFailure(Call<BagModel> call, Throwable t) {

            }
        });
    }

    private void getCustomer() {
        Retrofit retrofit = new RetrofitConfig().config();
        API api = retrofit.create(API.class);
        Call<CustomerModel> call = api.getCustomer(customerPk);

        call.enqueue(new Callback<CustomerModel>() {
            @Override
            public void onResponse(Call<CustomerModel> call, Response<CustomerModel> response) {
                customerModel = new CustomerModel();
                customerModel.setFirst_name(response.body().getFirst_name());
                customerModel.setLast_name(response.body().getLast_name());
                customerModel.setContact(response.body().getContact());
                customerModel.setEmail(response.body().getEmail());
                customerModel.setAddress(response.body().getAddress());
            }

            @Override
            public void onFailure(Call<CustomerModel> call, Throwable t) {

            }
        });
    }

    private void getRFIDTag() {
        final ProgressDialog progressDialog = new ProgressDialog(ClothActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.searching_for_tag));
        progressDialog.show();
        Retrofit retrofit = new RetrofitConfig().config();
        API api = retrofit.create(API.class);
        Call<List<RFIDModel>> listCall = api.getRFID();

        listCall.enqueue(new Callback<List<RFIDModel>>() {
            @Override
            public void onResponse(Call<List<RFIDModel>> call, Response<List<RFIDModel>> response) {
                try {
                    for (int i = 0; i < response.body().size(); i++) {
                        rfidModel = new RFIDModel();
                        rfidModel.setId(response.body().get(i).getId());
                        rfidModel.setTag(response.body().get(i).getTag());
                        tagID = response.body().get(i).getTag();
                    }
                }catch (Exception e){
                    Toast.makeText(ClothActivity.this, getString(R.string.no_tag), Toast.LENGTH_SHORT).show();
                }

                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<RFIDModel>> call, Throwable t) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(ClothActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
