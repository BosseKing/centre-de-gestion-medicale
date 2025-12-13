const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");

const medecinsRoutes = require("./routes/medecins.routes");

const app = express();
app.use(cors());
app.use(bodyParser.json());

app.use("/api/medecins", medecinsRoutes);

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`🚀 Service REST Médecins lancé sur http://localhost:${PORT}`);
});
