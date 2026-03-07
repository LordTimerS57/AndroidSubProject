CREATE TABLE location (
    numloc SERIAL PRIMARY KEY,
    nomloc VARCHAR(255) NOT NULL,
    design_voiture TEXT NOT NULL,
    nombre_jours INT NOT NULL,
    taux_journalier DECIMAL(10, 2) NOT NULL,
    loyer DECIMAL(10, 2)
);

----------------------------- FUNCTION for the rent ---------------------------------------- 

CREATE OR REPLACE FUNCTION loyer(nombre_jours INT, taux_journalier DECIMAL(10, 2))
RETURNS DECIMAL(10, 2) AS $$
BEGIN
    RETURN nombre_jours * taux_journalier;
END;
$$ LANGUAGE plpgsql;

----------------------- TRIGGER as the table "location" changes -----------------------------

--------------------- 1) CALLED FUNCTION when the trigger is on ------------------------------

CREATE OR REPLACE FUNCTION calcul_loyer_func()
RETURNS TRIGGER AS $$
BEGIN
    NEW.loyer := loyer(NEW.nombre_jours, NEW.taux_journalier);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

------------------------------ 2) The trigger itself ----------------------------------------

CREATE OR REPLACE TRIGGER calcul_loyer
BEFORE INSERT OR UPDATE ON location
FOR EACH ROW
EXECUTE FUNCTION calcul_loyer_func();


------------------------------------- TEST CASES --------------------------------------------

-- Test INSERT with trigger
INSERT INTO location (nomloc, design_voiture, nombre_jours, taux_journalier)
VALUES 
    ('Location 1', 'Tesla Model 3', 5, 50.00),
    ('Location 2', 'BMW X5', 10, 75.50),
    ('Location 3', 'Mercedes C-Class', 3, 100.00),
    ('Location 4', 'Audi A4', 0, 60.00),
    ('Location 5', 'Porsche 911', 365, 500.00);
    
-- Test SELECT from table
SELECT * FROM location;
