namespace API.Models
{
    public class Bird
    {
        public string? BirdId { get; set; }
        public string? Name { get; set; }
        public string? ScientificName { get; set; }
        public List<Picture>? pictures { get; set; }
    }
}