package com.noerkhalidah.ongkirin.ui.home;

import android.os.Handler;

import com.noerkhalidah.ongkirin.data.cost.DataType;
import com.noerkhalidah.ongkirin.data.cost.ResponseCost;
import com.noerkhalidah.ongkirin.network.Api;
import com.noerkhalidah.ongkirin.network.ApiEndpoint;
import com.noerkhalidah.ongkirin.network.Helper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class MainPresenter implements MainContract.Presenter {

    MainContract.View view;
    ApiEndpoint endpoint;

    String origin = "";
    String destination = "";
    int weight = 0;

    List<DataType> output = new ArrayList<>();
    List<String> courier = new ArrayList<>();

    public MainPresenter(MainContract.View view) {
        this.view = view;
        endpoint = Api.getUrl().create(ApiEndpoint.class);
    }

    @Override
    public void getJNE() {
        output.clear();
        courier.clear();
        endpoint.postCost(origin, destination, weight, "jne")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseCost>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseCost responseCost) {
                        for (DataType data : responseCost.getRajaongkir().getResults().get(0).getCosts()) {
                            output.add(data);
                            courier.add("JNE");
                        }
                        Helper.jne = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showMessage(e.getMessage());
                    }
                });
    }

    @Override
    public void getTIKI() {
        endpoint.postCost(origin, destination, weight, "tiki")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseCost>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseCost responseCost) {
                        for (DataType data : responseCost.getRajaongkir().getResults().get(0).getCosts()) {
                            output.add(data);
                            courier.add("TIKI");
                        }
                        Helper.tiki = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showMessage(e.getMessage());
                    }
                });
    }

    @Override
    public void getPOS() {
        endpoint.postCost(origin, destination, weight, "pos")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseCost>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ResponseCost responseCost) {
                        for (DataType data : responseCost.getRajaongkir().getResults().get(0).getCosts()) {
                            output.add(data);
                            courier.add("POS");
                        }

                        Helper.pos = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showMessage(e.getMessage());
                    }
                });
    }

    @Override
    public void setupENV(String origin, String destination, int weight) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;

        view.onLoadingCost(true, 25);

        getJNE();
        getPOS();
        getTIKI();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Helper.jne && Helper.pos && Helper.tiki) {
                    view.onLoadingCost(false, 100);
                    view.onResultCost(output, courier);
                }
            }
        }, 4000L);

    }
}
