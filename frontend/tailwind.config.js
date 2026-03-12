/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#1a56db',
        'primary-dark': '#1244b0',
      },
    },
  },
  plugins: [],
};

