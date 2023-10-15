namespace API.Models
{
    public class BirdSighting
    {
        public string? BirdSightingId { get; set; }
        public string? UserId { get; set; }
        public string? BirdId { get; set; }
        public DateTime? DateSeen { get; set; }
        public string? Coordinates { get; set; }
        public Picture? picture { get; set; }
    }
}