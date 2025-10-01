from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Image, Frame, PageTemplate
from reportlab.lib.styles import getSampleStyleSheet
from caso3directo.data_analytics import analytics

total_ventas, ventas_region, top_productos = analytics()

Marco='Marco.png'


styles= getSampleStyleSheet()

doc= SimpleDocTemplate('Reporte_semanal_ventas.pdf', pagesize=letter)

def add_marco(canvas,doc):
    canvas.saveState()
    # Ajustar el marco al tamaño de la página
    canvas.drawImage(Marco, 0, 0, width=letter[0], height=letter[1])
    canvas.restoreState()

frame = Frame(
    70, -10,
    letter[0]-100,
    letter[1]-100,
    id='normal'
)


template = PageTemplate(id='marco', frames=[frame], onPage=add_marco)
doc.addPageTemplates([template])


content=[]
content.append(Paragraph('REPORTE SEMANAL MAXSOFT VENTAS', styles['Title']))
content.append(Spacer(1,20))

content.append(Paragraph(f"Total de Ventas: ${total_ventas:,.2f} USD", styles["Heading2"]))
content.append(Spacer(1, 12))

content.append(Paragraph("Ventas por Región", styles["Heading2"]))
content.append(Image("../graphics/ventas_region.png", width=400, height=200))
content.append(Spacer(1, 12))

content.append(Paragraph("Top 5 Productos", styles["Heading2"]))
content.append(Image("../graphics/top_productos.png", width=400, height=200))


doc.build(content)

