const dotenv = require('dotenv');
dotenv.config();
module.exports = {
  hostname: process.env.PAYSTACK_BASE_URL || process.env.API_URL,
  secretKey: process.env.PAYSTACK_SECRET_KEY || process.env.API_KEY,
  port: process.env.PORT || 3000
};