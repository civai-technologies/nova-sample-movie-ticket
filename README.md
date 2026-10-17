# nova-sample-movie-ticket

Android movie browsing demo with checkout via **Nova Payments** hosted bank transfer (Custom Tabs), not the Paystack card SDK.

Forked from [PaystackOSS/sample-movie-ticket](https://github.com/PaystackOSS/sample-movie-ticket).

Docs: [Nova Payments](https://nova.civai.co/payments/docs)

## Nova checkout flow

1. Run the Node proxy in `server/node` (same as gift-store demo).
2. Set `PAYMENT_API_BASE` in `app/build.gradle` (`10.0.2.2:3000` for emulator → host machine).
3. Set `NOVA_PRODUCT_ID` to your Nova product id.
4. Payment screen calls `POST /transaction/initialize` on the proxy → opens `authorization_url` in **Chrome Custom Tabs**.

Configure the proxy with:

```env
PAYSTACK_BASE_URL=https://api.nova.civai.co/api/payments/compat/paystack
PAYSTACK_SECRET_KEY=your_nova_api_key
```

See `server/node/.env.example`.

## Movie DB API

You still need a [themoviedb.org](https://www.themoviedb.org/) API key in `gradle.properties` as upstream documented.
