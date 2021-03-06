/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workmarket.jan20;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;

/**
 * Common usage patterns when using the {@link Trial} framework.
 */
public final class TrialUtil {
  private TrialUtil() { }

  /**
   * Make a Callable&lt;Observable&lt;T&gt;&gt; from a Callable&lt;T&gt;.
   *
   * @param callable The callable to observableify.
   */
  public static <T> Callable<Observable<T>> makeObservable(final Callable<T> callable) {
    return new Callable<Observable<T>>() {
      @Override
      public Observable<T> call() throws Exception {
        return Observable.create(new Observable.OnSubscribe<T>() {
          @Override
          public void call(final Subscriber<? super T> subscriber) {
            try {
              final T result = callable.call();
              if (subscriber.isUnsubscribed()) {
                return;
              }
              subscriber.onNext(result);
              subscriber.onCompleted();
            } catch (final Throwable t) {
              if (!subscriber.isUnsubscribed()) {
                subscriber.onError(t);
              }
            }
          }
        });
      }
    };
  }
}
