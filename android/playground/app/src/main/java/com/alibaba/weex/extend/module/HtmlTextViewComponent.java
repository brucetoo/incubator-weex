/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.weex.extend.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alibaba.weex.extend.view.AtMostWebView;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.utils.WXLogUtils;

import org.sufficientlysecure.htmltextview.ClickableTableSpan;
import org.sufficientlysecure.htmltextview.DrawTableLinkSpan;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bruce Too
 * On 2019/1/5.
 * At 10:51
 */
public class HtmlTextViewComponent extends WXComponent<ScrollView> {

    public HtmlTextViewComponent(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    public HtmlTextViewComponent(WXSDKInstance instance, WXVContainer parent, int type, BasicComponentData basicComponentData) {
        super(instance, parent, type, basicComponentData);
    }

    class ClickableTableSpanImpl extends ClickableTableSpan {
        @Override
        public ClickableTableSpan newInstance() {
            return new ClickableTableSpanImpl();
        }

        @Override
        public void onClick(View widget) {
            Toast.makeText(getContext(), "Tap for table", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected ScrollView initComponentHostView(@NonNull final Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(layout,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        return scrollView;
    }

    class Component{
        public int type;
        public String info;
    }
    @WXComponentProp(name = "htmltext")
    public void setHtml(String htmltext){
        Pattern pattern = Pattern.compile("<table>.+?</table>");
        Matcher matcher = pattern.matcher(htmltext);

        String[] split = htmltext.split("<table>.+?</table>");
        List<Component> components = new ArrayList<>();
        if(split.length == 0){//不存在table
            components.add(getComponent(1, htmltext));
        }else {
            int index = 0;
            while (matcher.find()) {
                components.add(getComponent(1, split[index]));
                components.add(getComponent(2,matcher.group()));
                WXLogUtils.e("find one -> start:" + matcher.start() + " end:" + matcher.end());
                index++;
            }
            components.add(getComponent(1, split[index]));
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (Component component : components) {
            if(component.type == 1){
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                ((ViewGroup) getHostView().getChildAt(0)).addView(getTextView(component.info),params);
            }else {
                params.height = 300;
                ((ViewGroup) getHostView().getChildAt(0)).addView(getWebView(component.info),params);
            }
        }
    }

    private WebView getWebView(String info){
        AtMostWebView webView = new AtMostWebView(getContext());
        webView.loadDataWithBaseURL(null, info, "text/html", "utf-8", null);
        return webView;
    }

    private HtmlTextView getTextView(String info){
        HtmlTextView htmlTextView = new HtmlTextView(getContext());
        htmlTextView.setClickableTableSpan(new ClickableTableSpanImpl());
        DrawTableLinkSpan drawTableLinkSpan = new DrawTableLinkSpan();
        drawTableLinkSpan.setTableLinkText("[Tap for table]");
        htmlTextView.setDrawTableLinkSpan(drawTableLinkSpan);
        htmlTextView.setHtml(info,new HtmlHttpImageGetter(htmlTextView));
        return htmlTextView;
    }



    private Component getComponent(int type,String info){
        Component component = new Component();
        component.type = type;
        component.info = info;
        return component;
    }
}
