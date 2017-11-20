package com.Webtrekk.SDKTest.ProductList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.Webtrekk.SDKTest.R
import com.webtrekk.webtrekksdk.TrackingParameter
import com.webtrekk.webtrekksdk.Webtrekk
import kotlinx.android.synthetic.main.product_detail.*
import java.util.*

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Webtrekk GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Created by vartbaronov on 20.11.17.
 */

const val PRODUCT_ITEM: String = "PRODUCT_ITEM"

class ProductDetailActivity : AppCompatActivity(){
    private lateinit var model: ProductDetailModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_detail)

        model = ViewModelProviders.of(this).get(ProductDetailModel::class.java)

        if(savedInstanceState == null){
            val bundle = intent.getBundleExtra(PRODUCT_ITEM)

            model.getItem().observe(this, Observer { item ->
                var text = "Product position: "+ item?.ind + "\n"
                text += "Product id: "+ item?.id + "\n"
                text += "Product categories: "+ Arrays.toString(item?.categories) + "\n"
                text += "Product cost: "+ item?.cost + "\n"
                text += "Product ecom par: "+ Arrays.toString(item?.ecomParameters) + "\n"
                text += "Product paymentMethod: "+ item?.paymentMethod + "\n"
                text += "Product shippingService: "+ item?.shippingService + "\n"
                text += "Product shippingSpeed: "+ item?.shippingSpeed + "\n"
                text += "Product shippingCosts: "+ item?.shippingCosts + "\n"
                text += "Product grossMargin: "+ item?.grossMargin + "\n"
                text += "Product productVariant: "+ item?.productVariant + "\n"
                text += "Product productSoldOut: "+ item?.productSoldOut + "\n"
                //TODO add more parameters
                ProductDetails.text = text
            })

            model.getItem().value = ProductItem.getFromBundle(bundle)
        }

    }

    public fun onAdd(view: View){
        trackProduct("add")
    }

    public fun onView(view: View){
        trackProduct("view")
    }

    public fun onConf(view: View){
        trackProduct("conf")
    }

    private fun trackProduct(status: String){
        val parameter = TrackingParameter()
        val item = model.getItem().value
        parameter.add(TrackingParameter.Parameter.PRODUCT, item?.id)
        parameter.add(TrackingParameter.Parameter.PRODUCT_STATUS, status)
        Webtrekk.getInstance().track(parameter)
    }
}