from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Image, Frame, PageTemplate
from reportlab.lib.styles import getSampleStyleSheet
from caso3.analisis import analisis


total_ventas, ventas_region, top_productos= analisis()


# Ruta del marco corporativo
MARCO = "Marco.png"

# Definir estilo
styles = getSampleStyleSheet()

# Crear documento
doc = SimpleDocTemplate("Reporte_Semanal.pdf", pagesize=letter)

# --- 1) Función para dibujar el marco en cada página ---
def add_marco(canvas,doc):
    canvas.saveState()
    # Ajustar el marco al tamaño de la página
    canvas.drawImage(MARCO, 0, 0, width=letter[0], height=letter[1])
    canvas.restoreState()

# --- 2) Configurar plantilla con marco ---
frame = Frame(
    70, -10,             # margen X, Y
    letter[0]-100,      # ancho utilizable
    letter[1]-100,      # alto utilizable
    id="normal"
)

template = PageTemplate(id="marco", frames=[frame], onPage=add_marco)
doc.addPageTemplates([template])

# --- 3) Contenido del reporte ---
story = []
story.append(Paragraph("📊 Reporte Semanal de Ventas", styles["Title"]))
story.append(Spacer(1, 20))

story.append(Paragraph(f"Total de Ventas: ${total_ventas:,.2f}", styles["Heading2"]))
story.append(Spacer(1, 12))

story.append(Paragraph("Ventas por Región", styles["Heading2"]))
story.append(Image("../graficos/ventas_region.png", width=400, height=200))
story.append(Spacer(1, 12))

story.append(Paragraph("Top 5 Productos", styles["Heading2"]))
story.append(Image("../graficos/top_productos.png", width=400, height=200))

# --- 4) Construir documento ---
doc.build(story)
