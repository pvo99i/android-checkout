/*
 * Copyright 2014 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.checkout.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import org.solovyev.android.checkout.*;

import javax.annotation.Nonnull;

import static java.util.Arrays.asList;

public class MainActivity extends FragmentActivity {

	@Nonnull
	private final ActivityCheckout checkout = Checkout.forActivity(this, CheckoutApplication.get().getBilling(), asList(ProductTypes.IN_APP));

	@Nonnull
	private TextView purchasesCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			addFragment(new SkusListFragment(), R.id.fragment_skus_list, false);
		}
		purchasesCounter = (TextView) findViewById(R.id.purchases_counter);
		purchasesCounter.setText(getString(R.string.items_bought, 0));
		checkout.start();
		checkout.whenReady(new Checkout.ListenerAdapter() {
			@Override
			public void onReady(@Nonnull BillingRequests requests) {
				requests.getPurchases(ProductTypes.IN_APP, null, new RequestListenerAdapter<Purchases>() {
					@Override
					public void onSuccess(@Nonnull Purchases purchases) {
						purchasesCounter.setText(getString(R.string.items_bought, purchases.list.size()));
					}
				});
			}
		});
	}

	private void addFragment(@Nonnull Fragment fragment, int viewId, boolean retain) {
		fragment.setRetainInstance(retain);
		getSupportFragmentManager().beginTransaction()
				.add(viewId, fragment)
				.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		checkout.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		checkout.stop();
		super.onDestroy();
	}

	@Nonnull
	public ActivityCheckout getCheckout() {
		return checkout;
	}
}
