/*
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
package com.taobao.weex.utils.batch;

import com.taobao.weex.WXSDKEngine;

/**
 * Created by sospartan on 8/24/16.
 * 桥接到{@link com.taobao.weex.bridge.WXBridgeManager}中在 js线程中异步
 * 处理一些事情的接口，这些任务都是通过{@link com.taobao.weex.bridge.WXBridgeManager#post(Runnable)}出来的，
 * 并且可以通过{@link #setInterceptor(Interceptor)}，来控制是否执行某个任务
 *
 * 也就是说当通过WXBridgeManager执行异步任务时，当{@link Interceptor} 不为null时，都会优先执行
 * {@link Interceptor#take(Runnable)} 来决定是否拦截任务，而此逻辑的具体实现是在 {@link BatchOperationHelper#take(Runnable)}
 * 在{@link WXSDKEngine#register()}中发生组件注册的过程中，会有很多通过{@link com.taobao.weex.bridge.WXBridgeManager#post(Runnable)}
 * 来执行的异步任务，都是先添加到{@link BatchOperationHelper#sRegisterTasks}列表中，当注册完成后，通过
 * {@link BatchOperationHelper#flush()} 一次性执行全部任务，并且将{@link BatchOperationHelper#mExecutor}中的{@link Interceptor}
 * 设置为null，放开拦截的限制。因此此后的所有通过WXBridgeManager#post出来的任务都能立即正常执行
 */
public interface BactchExecutor {
  void post(Runnable runnable);

  void setInterceptor(Interceptor interceptor);
}
