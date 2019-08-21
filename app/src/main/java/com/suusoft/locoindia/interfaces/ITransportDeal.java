package com.suusoft.locoindia.interfaces;

import com.suusoft.locoindia.objects.TransportDealObj;

/**
 * Created by SuuSoft.com on 12/05/2016.
 */

public interface ITransportDeal {

    void onCancel(TransportDealObj obj);

    void onTracking(TransportDealObj obj);

    void onFinish(TransportDealObj obj);
}
