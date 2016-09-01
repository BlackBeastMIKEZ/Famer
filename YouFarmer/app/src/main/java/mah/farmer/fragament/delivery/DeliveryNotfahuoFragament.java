package mah.farmer.fragament.delivery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.locTest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mah.farmer.adapter.DeliveryAdapter;
import mah.farmer.bean.Delivery;
import mah.farmer.http.FarmerConfig;
import mah.farmer.view.CircleRefreshLayout;

/**显示未发货的购买订单的碎片
 * Created by 黑色野兽迈特祖 on 2016/5/2.
 */
public class DeliveryNotfahuoFragament extends Fragment {
    private Context ctx;// 从activity当中得到的上下文

    private List<Delivery> data;
    private ListView listView;
    private CircleRefreshLayout circleRefreshLayout;
    private DeliveryAdapter deliveryAdapter;
    private LinearLayout layout_all;
    public DeliveryNotfahuoFragament(Context ctx) {
        super();
        this.ctx = ctx;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        // 初始化fragment时使用
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // fragment创建时使用
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = View.inflate(ctx, R.layout.fragatment_delivery_not_fahuo, null);
        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        // activity创建时使用
        initView();

    }
    public void initView() {
        circleRefreshLayout= (CircleRefreshLayout) getActivity().findViewById(R.id.delivery_not_fahuo_refresh_layout);
        data=new ArrayList<>();
        listView= (ListView) getActivity().findViewById(R.id.delivery_not_fahuo_supply_list);
        getNotFahuodata();
        deliveryAdapter=new DeliveryAdapter(getActivity(),data);
        listView.setAdapter(deliveryAdapter);

    }


    public void getNotFahuodata() {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject json = new JSONObject();//创建json对象
        try {
            json.put("userNickName", FarmerConfig.getCachedFarmerNickname(getActivity()));
        }
        catch (Exception e){

        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,"http://192.168.235.22:8080/showPurchaseOrder/",

                json,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {

                    Toast.makeText(getActivity(), "获取购买订单" + jsonObject.getString("reason"), Toast.LENGTH_LONG).show();
                    JSONArray jsonarray=jsonObject.getJSONArray("data");
                    JSONObject products;
//                  Toast.makeText(FarmerPublishActivity.this, jsonarray.length()+" ", Toast.LENGTH_LONG).show();
                    data.clear();
                    for(int i=0;i<jsonarray.length();i++){
                        products=jsonarray.getJSONObject(i);
//                        Toast.makeText(FarmerPublishActivity.this,products.getString("productName"),Toast.LENGTH_LONG).show();
                        data.add(new Delivery(
                                FarmerConfig.getCachedFarmerNickname(getActivity()),
                                "wade",
                                products.getString("purchaseInfo"),
                                products.getString("purchaseOrderId"),
                                products.getString("productName"),
                                products.getString("purchaseNum"),
                                products.getString("purchasePrice"),
                                products.getString("youNongPrice")
                        ));
                    }
                    layout_all= (LinearLayout) getActivity().findViewById(R.id.layout_notfahuo_all);
                    if(data.size()==0){
                        layout_all.setVisibility(View.VISIBLE);
                        circleRefreshLayout.setVisibility(View.GONE);
                    }
                    else{
                        layout_all.setVisibility(View.GONE);
                        circleRefreshLayout.setVisibility(View.VISIBLE);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("网络错误")
                        .setContentText("获取订单失败")
                        .show();


            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}