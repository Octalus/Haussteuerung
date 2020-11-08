package de.infoscout.betterhome.view;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.view.utils.IabHelper;
import de.infoscout.betterhome.view.utils.IabResult;
import de.infoscout.betterhome.view.utils.Purchase;
import de.infoscout.betterhome.view.utils.Utilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class DonationActivity extends Activity {
	IabHelper mHelper = null;
	
	// Button setups
	private Button button_small, button_medium, button_large, button_xl, button_xxl;
	private Toast toast;
	
	static final String SKU_SMALL = "donation_small";
	static final String SKU_MEDIUM = "donation_medium";
	static final String SKU_LARGE = "donation_large";
	static final String SKU_XL = "donation_xl";
	static final String SKU_XXL = "donation_xxl";
	
	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_donation);
		
		// ...
	   String base64EncodedPublicKey = "IIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIvQVBBanlpyL7YwG6pZivflvgxOU8XymckWDnua4xzzi0hOA3I4he6ksq2VFmjemM7haKJpjR+X";
	   base64EncodedPublicKey += "0++PTOOwR5zYxnKRdj8P5m9PIxQJmWT/OaWLqH+bExIZC09XKdR+BP+gHY5S4T/IUENdRVpOnGMXetAlJTHmALuQGY9LKI2o9TgRDIBzG/MBVAwl6JSZjSyTnW";
	   base64EncodedPublicKey += "l6FfeY0HxEMLjFtEcxAF5wAgzbkx4HhA35OgqEzcUdCawZLzWnUQz3B7jiHORW9L34aJQBB9mvHePKLzyeFjIooAcpiGlRw+jJShTT+z2i7xVkBOYjQRFxLF6/MAQG900ST6miDSfHJMwUWwIDAQAB";
	   
	   // compute your public key and store it in base64EncodedPublicKey
	   mHelper = new IabHelper(this, "M"+base64EncodedPublicKey);
	   mHelper.enableDebugLogging(false);
	   
	   mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(Utilities.TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					toast(getString(R.string.error_verification) + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we own.
				//   --commented out here as we didn't need it for donation purposes.
				// Log.d(TAG, "Setup successful. Querying inventory.");
				// mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	   
	   
	   button_small = (Button) findViewById(R.id.donate_button_small);
		button_medium = (Button) findViewById(R.id.donate_button_medium);
		button_large = (Button) findViewById(R.id.donate_button_large);
		button_xl = (Button) findViewById(R.id.donate_button_xl);
		button_xxl = (Button) findViewById(R.id.donate_button_xxl);

		button_small.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeDonation(1);
			}
		});

		button_medium.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeDonation(2);
			}
		});

		button_large.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeDonation(3);
			}
		});

		button_xl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeDonation(4);

			}
		});

		button_xxl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				makeDonation(5);
			}
		});
		
	}
	
	//the button clicks send an int value which would then call the specific SKU, depending on the 
		//application
		public void makeDonation(int value) {
			//check your own payload string.
			String payload = "";

			switch (value) {
			case (1):
				mHelper.launchPurchaseFlow(this, SKU_SMALL, RC_REQUEST,
						mPurchaseFinishedListener, payload);
				System.out.println("small purchase");
				break;
			case (2):
				mHelper.launchPurchaseFlow(this, SKU_MEDIUM, RC_REQUEST,
						mPurchaseFinishedListener, payload);
				System.out.println("medium purchase");
				break;
			case (3):
				mHelper.launchPurchaseFlow(this, SKU_LARGE, RC_REQUEST,
						mPurchaseFinishedListener, payload);
				System.out.println("large purchase");
				break;
			case (4):
				System.out.println("xl purchase");
				mHelper.launchPurchaseFlow(this, SKU_XL, RC_REQUEST,
						mPurchaseFinishedListener, payload);
				break;
			case (5):
				System.out.println("xxl purchase");
				mHelper.launchPurchaseFlow(this, SKU_XXL, RC_REQUEST,
						mPurchaseFinishedListener, payload);
				break;

			default:
				break;
			}

		}
	
	//DO NOT SKIP THIS METHOD
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(Utilities.TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(Utilities.TAG, "onActivityResult handled by IABUtil.");
		}
	}
	
	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/**Follow google guidelines to create your own payload string here, in case it is needed.
		*Remember it is recommended to store the keys on your own server for added protection
		USE as necessary*/

		return true;
	}
	
	// Called when consumption is complete
		IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
			public void onConsumeFinished(Purchase purchase, IabResult result) {
				Log.d(Utilities.TAG, "Consumption finished. Purchase: " + purchase
						+ ", result: " + result);

				// if we were disposed of in the meantime, quit.
				if (mHelper == null)
					return;

				//check which SKU is consumed here and then proceed.
				
				if (result.isSuccess()) {
					
					Log.d(Utilities.TAG, "Consumption successful. Provisioning.");

					toast(getString(R.string.thank_you));
				} else {
					toast(getString(R.string.error_consume) + result);
				}
				
				
				Log.d(Utilities.TAG, "End consumption flow.");
			}
		};
	
	// Callback for when a purchase is finished
		IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
			public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
				Log.d(Utilities.TAG, "Purchase finished: " + result + ", purchase: "
						+ purchase);

				// if we were disposed of in the meantime, quit.
				if (mHelper == null)
					return;

				if (result.isFailure()) {
					toast(getString(R.string.purchase_error) + result);
					// setWaitScreen(false);
					return;
				}
				if (!verifyDeveloperPayload(purchase)) {
					toast(getString(R.string.error_verification));
					// setWaitScreen(false);
					return;
				}

				Log.d(Utilities.TAG, "Purchase successful.");

				if (purchase.getSku().equals(SKU_SMALL)
						|| purchase.getSku().equals(SKU_MEDIUM)
						|| purchase.getSku().equals(SKU_LARGE)
						|| purchase.getSku().equals(SKU_XL)
						|| purchase.getSku().equals(SKU_XXL)) {

					// Log.d(TAG, "small donation");
					mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				}

			}
		};
		
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}
	
	private void toast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(getApplicationContext(), "",
							Toast.LENGTH_SHORT);
				}
				toast.setText(msg);
				toast.show();
			}
		});
	}
	
}
